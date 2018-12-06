package org.jro.exercise.akkastream.jhchallenge;

import akka.Done;
import akka.actor.ActorSystem;
import akka.actor.Terminated;
import akka.stream.*;

import org.slf4j.LoggerFactory;
import scala.Tuple4;
import scala.util.Try;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.BiConsumer;
import org.slf4j.Logger;


public class Main {
	public static final Object UNIT = new Object();
	public static final Logger appLogger = LoggerFactory.getLogger("App");

	public static void main(String[] args) {
		final ActorSystem system = ActorSystem.create("Akka_Stream_hands-on");
		final int parallelism = parseParallelism(args.length >= 1 ? args[0] : "1");

		try{
			final Materializer materializer = ActorMaterializer.create(system);
			HChallenge challenge = new HChallenge(0, 99999999, Utils.hexStringToBytes("dae1d529b16ad4af420f4fd54840a0e4"));
			//HChallengeBuilder.runSimpleScan(challenge, materializer).whenComplete(completion(LocalDateTime.now(), system));
			//HChallengeBuilder.runSimpleScanWithGraph(challenge, materializer).whenComplete(completion(LocalDateTime.now(), system));
			HChallengeBuilder.runParallelScanWithGraph(challenge, parallelism, materializer).whenComplete(completion(LocalDateTime.now(), system));
		} catch (Exception ex){
			system.terminate().onComplete(triedTerminated -> {
				if(triedTerminated.isSuccess())
					appLogger.warn("Actor system {} terminated because of error {}", system.name(), ex);
				else
					appLogger.error(String.format("Actor system %s termination failure:", system.name()), ex);
				return UNIT;
			}, system.dispatcher());
		}
	}

	public static int parseParallelism(String arg){
		try {
			return Integer.parseInt(arg);
		} catch (NumberFormatException ex) {
			appLogger.warn("{} is not an integer; assuming 1 as level of parallelism", arg);
			return 1;
		}
	}

	public static Object logTermination(Try<Terminated> arg) {
		if(arg.isSuccess()) {
			appLogger.info("Actor system terminated");
		} else {
			appLogger.error("While terminating actors:", arg.failed().get());
		}
		return UNIT;
	}

	public static Tuple4<Long, Long, Long, Long> duration(LocalDateTime startTime, LocalDateTime endTime) {
		LocalDateTime tempDT = LocalDateTime.from(startTime);

		final Long hours = tempDT.until(endTime, ChronoUnit.HOURS);
		tempDT = tempDT.plusHours(hours);

		final Long minutes = tempDT.until(endTime, ChronoUnit.MINUTES);
		tempDT = tempDT.plusMinutes(minutes);

		final Long seconds = tempDT.until(endTime, ChronoUnit.SECONDS);
		tempDT = tempDT.plusSeconds(seconds);

		final Long ms = tempDT.until(endTime, ChronoUnit.MILLIS);

		return new Tuple4<>(hours, minutes, seconds, ms);
	}

	public static String formatDuration(Tuple4<Long, Long, Long, Long> duration) {
		return String.format("Elapsed: %sh %smin %s.%ss", duration._1(), duration._2(), duration._3(), duration._4());
	}

	public static BiConsumer<Done, Throwable> completion(LocalDateTime startTime, ActorSystem sys) {
		return (Done d, Throwable err) -> {
			if(err == null) {
				appLogger.info(
						"Stream completed successfully, terminating actor system. {}",
						formatDuration(duration(startTime, LocalDateTime.now()))
				);
			} else {
				appLogger.error(
						String.format(
								"Stream complete, terminating actor system. {}. Stream terminated with error: {}",
								formatDuration(duration(startTime, LocalDateTime.now()))
						),
						err
				);
			}
			sys.terminate().onComplete(Main::logTermination, sys.dispatcher());
		};
	}
}

package org.jro.exercise.akkastream.shchallenge

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

import akka.actor.{ActorSystem, Terminated}
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

object App extends App {
  val config = ConfigFactory.load()
  val appLogger = Logger("App")

  //Ugly top-level catch-all to avoid missing any err
  Try(ActorSystem("Akka_Stream_hands-on", config)).map(sys => (sys, Try(ActorMaterializer()(sys)))).flatMap {
    case (sys, Success(mat)) =>
      implicit val iSys: ActorSystem = sys
      implicit val iMat: ActorMaterializer = mat
      implicit val execCtxt: ExecutionContext = sys.dispatcher
      val challenge = new HChallenge(0 to 99999999, Utils.hexStringToBytes("dae1d529b16ad4af420f4fd54840a0e4"))
      //TODO 1.4, 2.3, 3.6: call HChallengeBuilder.run... depending on what stream you want to run.
      //1.4:
      //HChallengeBuilder.runSimpleScan(challenge).onComplete(completion(LocalDateTime.now()))
      //2.3:
      //HChallengeBuilder.runSimpleScanWithGraph(challenge).onComplete(completion(LocalDateTime.now()))
      //3.6:
      HChallengeBuilder.runParallelScanWithGraph(
        challenge,
        args.headOption.map(Integer.parseInt).getOrElse(1)
      ).onComplete(completion(LocalDateTime.now()))

      Success(sys, mat)
    case (sys, Failure(err)) =>
      sys.terminate()
      Failure(err)
  }.recoverWith {
    case err =>
      appLogger.error("App initialization failed with", err)
      Failure(err)
  }

  def logTermination(arg: Try[Terminated]): Unit = {
    arg match {
      case Success(_) => appLogger.info("Actor system terminated")
      case Failure(err) => appLogger.error("While terminating actors:", err)
    }
  }

  def completion(startTime: LocalDateTime)(done: Try[_])(implicit sys: ActorSystem) = done match {
    case Success(_) =>
      appLogger.info(s"Stream complete, terminating actor system. Stream successful. ${formatDuration(duration(startTime, LocalDateTime.now))}")
      sys.terminate().onComplete(logTermination)(sys.dispatcher)
    case Failure(err) =>
      appLogger.error(s"Stream complete, terminating actor system. ${formatDuration(duration(startTime, LocalDateTime.now))}. Stream terminated with error:", err)
      sys.terminate().onComplete(logTermination)(sys.dispatcher)
  }

  def duration(startTime: LocalDateTime, endTime: LocalDateTime): (Long, Long, Long, Long) = {
    var tempDT = LocalDateTime.from(startTime)

    val hours = tempDT.until(endTime, ChronoUnit.HOURS)
    tempDT = tempDT.plusHours(hours)

    val minutes = tempDT.until(endTime, ChronoUnit.MINUTES)
    tempDT = tempDT.plusMinutes(minutes)

    val seconds = tempDT.until(endTime, ChronoUnit.SECONDS)
    tempDT = tempDT.plusSeconds(seconds)

    val ms = tempDT.until(endTime, ChronoUnit.MILLIS)

    (hours, minutes, seconds, ms)
  }

  def formatDuration(duration: (Long, Long, Long, Long)) = {
    val (hours, minutes, seconds, ms) = duration
    s"Elapsed: ${hours}h ${minutes}min ${seconds}.${ms}s"
  }

}

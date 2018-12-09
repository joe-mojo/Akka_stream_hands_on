package org.jro.exercise.akkastream.jhchallenge;

import akka.Done;
import akka.NotUsed;
import akka.stream.ClosedShape;
import akka.stream.Graph;
import akka.stream.Materializer;
import akka.stream.Outlet;
import akka.stream.javadsl.GraphDSL;
import akka.stream.javadsl.RunnableGraph;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import scala.Tuple2;

import java.util.concurrent.CompletionStage;

public class HChallengeBuilder {

	public static Sink<Tuple2<String, byte[]>, CompletionStage<Done>> createProgressSink(HChallenge challenge) {
		return createProgressSink(challenge, 1);
	}

	public static Sink<Tuple2<String, byte[]>, CompletionStage<Done>> createProgressSink(HChallenge challenge, Integer line) {
		return Sink.foreach(hashEntry -> {
			if(challenge.rightHash(hashEntry))
				System.out.println('\r' + Utils.txtFound(hashEntry, line));
			else if(hashEntry._1.endsWith("00000]"))
				System.out.print('\r' + Utils.progress(hashEntry, line));
			System.out.flush();
		});
	}

	public static CompletionStage<Done> runSimpleScan(HChallenge challenge, Materializer matzr) {
		return challenge.simpleScan().runWith(createProgressSink(challenge), matzr);
	}

	public static <Mat1, Mat2> Graph<ClosedShape, Mat2> createSimpleScanGraph(
			Source<Tuple2<String, byte[]>, Mat1> source,
			Sink<Tuple2<String, byte[]>, Mat2> sink
	) {
		/* TODO 2.1: create a simple  with GraphDSL that do exactly the same as HChallenge.simpleScan completed at 1.1
		   Hint: you need only to connect the source and the sink
		 */
		return null;
	}

	public static CompletionStage<Done> runSimpleScanWithGraph(HChallenge challenge, Materializer matzr) {
		//TODO 2.2: create a source, a sink and pass them to createSimpleScanGraph in order to create the graph, then run it
		return null;
	}

	public static <Mat2> Graph<ClosedShape, Mat2> createParallelScanGraph(HChallenge challenge, int par, Sink<Tuple2<String, byte[]>, Mat2> sink) {
		//TODO 3.4: create a graph with a source, parallel flows, and a sink. Don't forget that "par" argument is the number of parallel flows. Don't forget it must stop when the tarhet hash is found.
		//Hint: use GraphElements.parallelHashFlow to create a simple Flow that holds the parallel flows
		return null;
	}

	public static CompletionStage<Done> runParallelScanWithGraph(HChallenge challenge, int par, Materializer matzr) {
		//TODO 3.5: create graph with the requested level of parallelism and tun it with the "progress sink"
		return null;
	}

}

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
		return GraphDSL.create(
				sink,
				(builder, out) -> {
					final Outlet<Tuple2<String, byte[]>> sourceOut =  builder.add(source).out();
					builder.from(sourceOut).to(out);
					return ClosedShape.getInstance();
				}
		);
	}

	public static CompletionStage<Done> runSimpleScanWithGraph(HChallenge challenge, Materializer matzr) {
		return RunnableGraph.fromGraph(createSimpleScanGraph(challenge.simpleScan(), createProgressSink(challenge))).run(matzr);
	}

	public static <Mat2> Graph<ClosedShape, Mat2> createParallelScanGraph(HChallenge challenge, int par, Sink<Tuple2<String, byte[]>, Mat2> sink) {
		return GraphDSL.create(
				sink,
				(builder, out) -> {
					final Outlet<Tuple2<String, byte[]>> sourceOut = builder.add(
							GraphElements.source(challenge.minInput, challenge.maxInput)
							.via(GraphElements.parallelHashFlow(par).takeWhile(challenge::wrongHash, true))
					).out();
					builder.from(sourceOut).to(out);
					return ClosedShape.getInstance();
				}
		);
	}

	public static CompletionStage<Done> runParallelScanWithGraph(HChallenge challenge, int par, Materializer matzr) {
		return RunnableGraph.fromGraph(createParallelScanGraph(challenge, par, createProgressSink(challenge))).run(matzr);
	}

}

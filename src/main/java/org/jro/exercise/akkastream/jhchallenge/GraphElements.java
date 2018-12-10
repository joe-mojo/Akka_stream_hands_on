package org.jro.exercise.akkastream.jhchallenge;

import akka.NotUsed;
import akka.stream.FlowShape;
import akka.stream.UniformFanInShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.*;
import scala.Tuple2;

public class GraphElements {

	public static Flow<Integer, Tuple2<String, byte[]>, NotUsed> hashFlow() {
		return Flow.fromFunction((i) -> HChallenge.hashEntry(Utils.wrap(i)));
	}

	public static Source<Integer, NotUsed> source(Integer min, Integer max) {
		return Source.range(min, max);
	}

	public static Flow<Integer, Tuple2<String, byte[]>, NotUsed> parallelHashFlow(int parts) {
		return Flow.fromGraph(GraphDSL.create(builder -> {
				final UniformFanOutShape<Integer, Integer> dispatchIntegers = builder.add(Balance.create(parts));
				final UniformFanInShape<Tuple2<String, byte[]>, Tuple2<String, byte[]>> mergeHashEntries = builder.add(Merge.create(parts));
				for(int p = 0; p < parts; p++) {
					builder.from(dispatchIntegers).via(builder.add(hashFlow().async())).toFanIn(mergeHashEntries);
				}
				return FlowShape.of(dispatchIntegers.in(), mergeHashEntries.out());
			})
		);
	}

}

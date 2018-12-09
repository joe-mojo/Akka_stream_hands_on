package org.jro.exercise.akkastream.jhchallenge;

import akka.NotUsed;
import akka.stream.FlowShape;
import akka.stream.UniformFanInShape;
import akka.stream.UniformFanOutShape;
import akka.stream.javadsl.*;
import scala.Tuple2;

public class GraphElements {

	public static Flow<Integer, Tuple2<String, byte[]>, NotUsed> hashFlow() {
		//TODO 3.1: create a flow that take an Int as input and ouputs Tuple2<String, byte[]> where the string is the hash input and the byte[] is the hash value.
		//Hint: create a flow from a function
		return Flow.fromFunction((i) -> HChallenge.hashEntry(Utils.wrap(i)));
	}

	public static Source<Integer, NotUsed> source(Integer min, Integer max) {
		//TODO 3.2 create a source of numbers. DEAD simple, don't look for something complicated ;)
		return null;
	}

	public static Flow<Integer, Tuple2<String, byte[]>, NotUsed> parallelHashFlow(int parts) {
		/* TODO 3.3 Create an open graph that connect the parallel flows :
          - create balance using as much outputs as specified by "parts" argument
          - create a merge using as much inputs as balance outputs
          - connect balance, flow and merge. Don't forget that this must be done as much time as...
          - return the right shape (not a closed shape)
       */
		return null;
	}

}

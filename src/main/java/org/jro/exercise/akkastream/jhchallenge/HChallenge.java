package org.jro.exercise.akkastream.jhchallenge;


import akka.NotUsed;
import akka.stream.javadsl.Source;
import scala.Tuple2;
import static org.jro.exercise.akkastream.jhchallenge.Utils.*;

import java.util.Arrays;

public class HChallenge {
	public final Integer minInput;
	public final Integer maxInput;
	public final byte[] targetHash;

	public HChallenge(Integer minInput, Integer maxInput, byte[] targetHash) {
		this.minInput = minInput;
		this.maxInput = maxInput;
		this.targetHash = targetHash;
	}

	public Source<Tuple2<String, byte[]>, NotUsed> simpleScan() {
		return Source.range(minInput, maxInput).map(Utils::wrap).map(HChallenge::hashEntry).takeWhile(this::wrongHash, true);
	}

	public static Tuple2<String, byte[]> hashEntry(String input) {
		return Tuple2.apply(input, md5sum(input));
	}

	public boolean rightHash(Tuple2<String, byte[]> hashEntry) {
		return Arrays.equals(hashEntry._2, targetHash);
	}

	public boolean wrongHash(Tuple2<String, byte[]> hashEntry) {
		return ! rightHash(hashEntry);
	}


}

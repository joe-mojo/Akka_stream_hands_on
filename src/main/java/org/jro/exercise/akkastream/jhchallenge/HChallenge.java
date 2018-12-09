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
		/* TODO 1.1 create a source of Int mapped to (String, Array[Byte]) where the string is the hash input and the Array is the hash value. Don't forget it must stop when the target hash is found.
		   Sorry, in Java a (String, Array[Byte]) is a Tuple2<String, byte[]> :/
		   Hint : use function in Utils
		*/
		return null;
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

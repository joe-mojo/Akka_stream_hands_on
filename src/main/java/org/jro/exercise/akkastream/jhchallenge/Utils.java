package org.jro.exercise.akkastream.jhchallenge;

import scala.Console;
import scala.Tuple2;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	//def hexStringToBytes(hex: String): Array[Byte] = hex.sliding(2, 2).map(Integer.parseInt(_, 16).toByte).toArray
	public static byte[] hexStringToBytes(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for(int i = 0, len = hex.length() - 1; i < len; i += 2) {
			String byteStr = hex.substring(i, i + 2);
			bytes[i/2] = Integer.valueOf(Integer.parseInt(byteStr, 16)).byteValue();
		}
		return bytes;
	}

	public static byte[] md5sum(String input) {
		try {
			return MessageDigest.getInstance("MD5").digest(input.getBytes(StandardCharsets.US_ASCII));
		} catch (NoSuchAlgorithmException nsaex) {
			throw new RuntimeException(nsaex);
		}
	}

	public static String txtFound(Tuple2<String, byte[]> hashEntry) {
		return txtFound(hashEntry, 1);
	}

	public static String txtFound(Tuple2<String, byte[]> hashEntry, Integer line) {
		return String.format(
				"\u001B[s\u001B[%4$s;1H%4$s) %1$s * FOUND * %2$s%5$s%3$s --> %2$s%6$s%3$s\u001B[u",
				Console.GREEN(),
				Console.CYAN(),
				Console.RESET(),
				line,
				hashEntry._1,
				bytesToHexString(hashEntry._2)
		);
	}

	public static String progress(Tuple2<String, byte[]> hashEntry) {
		return progress(hashEntry, 1);
	}

	public static String progress(Tuple2<String, byte[]> hashEntry, Integer line) {
		return String.format(
				"\u001B[s\u001B[%1$s;1H%1$s) %2$scurrent progress: %3$s%4$s%5$s --> %3$s%6$s%5$s\u001B[u",
				line,
				Console.YELLOW(),
				Console.BLUE(),
				hashEntry._1,
				Console.RESET(),
				bytesToHexString(hashEntry._2)
		);
	}

	public static String bytesToHexString(byte[] bytes) {
		final StringBuilder hexStrBuilder = new StringBuilder(bytes.length * 2);
		for(int i=0, l=bytes.length; i<l; i++) {
			hexStrBuilder.append(String.format("%02x", bytes[i]));
		}
		return  hexStrBuilder.toString();
	}

	public static String wrap(Integer number) {
		String numberStr = number.toString();
		int numberStrLen = numberStr.length();
		return String.format("[%s]", "00000000".substring(0, 8 - numberStrLen) + numberStr);
	}
	/*
	def wrap(number: Int): String = s"[${number.toString.reverse.padTo(8, '0').reverse}]"
	 */
}

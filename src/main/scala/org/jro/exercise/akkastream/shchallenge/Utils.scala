package org.jro.exercise.akkastream.shchallenge

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

import akka.util.ByteString

object Utils {
  def hexStringToBytes(hex: String): Array[Byte] = hex.sliding(2, 2).map(Integer.parseInt(_, 16).toByte).toArray
  def bytesToHexString(bytes: Array[Byte]): String = bytes.map("%02x" format _).mkString
  def bytesToHexString(bytes: ByteString): String = bytes.map("%02x" format _).mkString
  def md5sum(input: String): Array[Byte] = MessageDigest.getInstance("MD5").digest(input.getBytes(StandardCharsets.US_ASCII))
  def wrap(number: Int): String = s"[${number.toString.reverse.padTo(8, '0').reverse}]"

}

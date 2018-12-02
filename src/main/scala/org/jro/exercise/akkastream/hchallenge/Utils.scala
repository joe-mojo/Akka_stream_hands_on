package org.jro.exercise.akkastream.hchallenge

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

import akka.util.ByteString

object Utils {
  def hexStringToBytes(hex: String): Array[Byte] = hex.sliding(2, 2).map(Integer.parseInt(_, 16).toByte).toArray
  def bytesToHexString(bytes: Array[Byte]): String = bytes.map("%02x" format _).mkString
  def bytesToHexString(bytes: ByteString): String = bytes.map("%02x" format _).mkString
  def md5sum(input: String): Array[Byte] = MessageDigest.getInstance("MD5").digest(input.getBytes(StandardCharsets.US_ASCII))
  def wrap(number: Int): String = s"[${number.toString.reverse.padTo(8, '0').reverse}]"

  object MessageBuilder {
    def txtFound(hashEntry: (String, Array[Byte]), line: Int = 1): String = {
      s"\u001B[s\u001B[${line};1H${line}) ${Console.GREEN} * FOUND * ${Console.CYAN}${hashEntry._1}${Console.RESET} --> ${Console.CYAN}${bytesToHexString(hashEntry._2)}${Console.RESET}\u001B[u"
    }
    def progress(hashEntry: (String, Array[Byte]), line: Int = 1): String = {
      s"\u001B[s\u001B[${line};1H${line}) ${Console.YELLOW}current progress: ${Console.BLUE}${hashEntry._1}${Console.RESET} --> ${Console.BLUE}${bytesToHexString(hashEntry._2)}${Console.RESET}\u001B[u"
    }
  }
}

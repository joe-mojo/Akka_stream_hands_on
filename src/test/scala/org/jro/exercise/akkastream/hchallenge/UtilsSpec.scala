package org.jro.exercise.akkastream.hchallenge

import akka.util.ByteString
import org.scalatest.{Matchers, WordSpec}

class UtilsSpec extends WordSpec with Matchers {
  val targetHashAsArray: Array[Byte] = Array(-38, -31, -43, 41, -79, 106, -44, -81, 66, 15, 79, -43, 72, 64, -96, -28)
  val targetHashAsHexString: String = "dae1d529b16ad4af420f4fd54840a0e4"

  "hexStringToBytes" should {
    "parse bytes from an hex string" in {
      Utils.hexStringToBytes(targetHashAsHexString) should be(targetHashAsArray)
    }
  }

  "bytesToHexString" when {
    "called with an Array of Bytes" should {
      "format bytes to an hex string" in {
        Utils.bytesToHexString(targetHashAsArray) should be(targetHashAsHexString)
      }
    }
    "called with a ByteString" should {
      "format bytes to an hex string" in {
        Utils.bytesToHexString(ByteString(targetHashAsArray)) should be(targetHashAsHexString)
      }
    }
  }

  "wrap" should {
    "pad number up to length 8 and add brackets around" in {
      Utils.wrap(0) should be("[00000000]")
      Utils.wrap(1) should be("[00000001]")
      Utils.wrap(12) should be("[00000012]")
      Utils.wrap(12345678) should be("[12345678]")
      Utils.wrap(99999999) should be("[99999999]")
      Utils.wrap(123456789) should be("[123456789]")
    }
  }
  "md5sum" should {
    "compute the expected md5 hash for the challenge answer" in {
      Utils.md5sum("[41414141]") should be(targetHashAsArray)
    }
  }
}

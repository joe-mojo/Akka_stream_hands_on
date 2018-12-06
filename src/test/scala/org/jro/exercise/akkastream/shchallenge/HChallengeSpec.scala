package org.jro.exercise.akkastream.shchallenge

import org.scalatest._
import Matchers._
import org.jro.exercise.akkastream.shchallenge.{HChallenge, Utils}

class HChallengeSpec extends WordSpec with Matchers with Inside{
  val targetHashAsArray: Array[Byte] = Array(-38, -31, -43, 41, -79, 106, -44, -81, 66, 15, 79, -43, 72, 64, -96, -28)
  val targetHashAsHexString: String = "dae1d529b16ad4af420f4fd54840a0e4"
  val answerString = "[41414141]"

  "HChallenge companion object" when {

    "wrongHash is called on challenge answer string" should {
      "reply false" in {
        HChallenge.wrongHash(targetHashAsArray)(answerString -> targetHashAsArray) should be(false)
        val targetHashAsArrayCopy = Array(targetHashAsArray: _*)
        HChallenge.wrongHash(targetHashAsArrayCopy)(answerString -> targetHashAsArray) should be(false)
      }
    }

    "wrongHash is called on wrong string" should {
      "reply true" in {
        HChallenge.wrongHash(targetHashAsArray)("[89600000]" -> Utils.hexStringToBytes("fd80d2b94f2e49861acfd948c2584d26")) should be(true)
        val targetHashAsArrayCopy = Array(targetHashAsArray: _*)
        HChallenge.wrongHash(targetHashAsArrayCopy)("[89600000]" -> Utils.hexStringToBytes("fd80d2b94f2e49861acfd948c2584d26")) should be(true)
      }
    }

    "rightHash is called on challenge answer string" should {
      "reply true" in {
        HChallenge.rightHash(targetHashAsArray)(answerString -> targetHashAsArray) should be(true)
        val targetHashAsArrayCopy = Array(targetHashAsArray: _*)
        HChallenge.rightHash(targetHashAsArrayCopy)(answerString -> targetHashAsArray) should be(true)
      }
    }

    "rightHash is called on wrong string" should {
      "reply false" in {
        HChallenge.rightHash(targetHashAsArray)("[89600000]" -> Utils.hexStringToBytes("fd80d2b94f2e49861acfd948c2584d26")) should be(false)
        val targetHashAsArrayCopy = Array(targetHashAsArray: _*)
        HChallenge.rightHash(targetHashAsArrayCopy)("[89600000]" -> Utils.hexStringToBytes("fd80d2b94f2e49861acfd948c2584d26")) should be(false)
      }
    }

  }

  private def checkInputRanges(expectedPartCount: Int, actualChallenges: Seq[HChallenge]) = {
    actualChallenges should have length expectedPartCount

  }

  "HChallenge instance" when {
    "sliced into parts" should {
      "give the right number of parts of equivalent sizes" in {
        val challenge = HChallenge(0 to 100, targetHashAsArray)
        val smallerChallenges = challenge.cutIn(10)
        checkInputRanges(10, smallerChallenges)
        val smallerOddChallenges = challenge.cutIn(11)
        checkInputRanges(11, smallerOddChallenges)
        val smallerEvenChallenges = challenge.cutIn(12)
        checkInputRanges(12, smallerEvenChallenges)
      }
    }
    "sliced into more parts than range size" should {
      "give as much part as range size" in {
        val challenge = HChallenge(0 to 10, targetHashAsArray)
        val smallerChallenges = challenge.cutIn(12)
        smallerChallenges should have length 11
        checkInputRanges(11, smallerChallenges)
        val smallerOddChallenges = challenge.cutIn(11)
        checkInputRanges(11, smallerOddChallenges)
      }
    }
  }
}

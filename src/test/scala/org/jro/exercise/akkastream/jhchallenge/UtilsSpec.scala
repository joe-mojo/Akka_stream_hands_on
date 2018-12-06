package org.jro.exercise.akkastream.jhchallenge

import org.scalatest.{Matchers, WordSpec}

class UtilsSpec extends WordSpec with Matchers{
	val targetHashAsArray: Array[Byte] = Array(-38, -31, -43, 41, -79, 106, -44, -81, 66, 15, 79, -43, 72, 64, -96, -28)
	val targetHashAsHexString: String = "dae1d529b16ad4af420f4fd54840a0e4"

	"hexStringToBytes" should {
		"parse bytes from an hex string" in {
			Utils.hexStringToBytes(targetHashAsHexString) should be(targetHashAsArray)
		}
	}
}

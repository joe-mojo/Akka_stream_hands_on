package org.jro.exercise.akkastream.hchallenge

import akka.stream.scaladsl.{Balance, Flow, GraphDSL, Merge, RunnableGraph, Sink, Source}
import Utils.{md5sum, _}
import akka.{Done, NotUsed}
import akka.stream._
import HChallenge.{rightHash, wrongHash}

import scala.annotation.tailrec
import scala.concurrent.Future

case class HChallenge(inputRange: Range, targetHash: Array[Byte]) {
  import HChallenge.{wrongHash, hashEntry}
  def simpleScan: Source[(String, Array[Byte]), NotUsed] = {
    Source(inputRange).map(wrap).map(hashEntry).takeWhile(wrongHash(targetHash), inclusive = true)
  }

}

object HChallenge {
  def wrongHash(targetHash: Array[Byte])(hashEntry: (String, Array[Byte])): Boolean = ! rightHash(targetHash)(hashEntry)
  def rightHash(targetHash: Array[Byte])(hashEntry: (String, Array[Byte])): Boolean = hashEntry._2 sameElements targetHash
  def hashEntry(input: String): (String, Array[Byte]) = (input, md5sum(input))
}


object HChallengeBuilder {
  def createProgressSink(challenge: HChallenge): Sink[(String, Array[Byte]), Future[Done]] = Sink.foreach{ (hashEntry: (String, Array[Byte])) =>
    if(rightHash(challenge.targetHash)(hashEntry)) println(s"\r${Console.GREEN} * FOUND * ${Console.CYAN}${hashEntry._1}${Console.RESET} --> ${Console.CYAN}${bytesToHexString(hashEntry._2)}${Console.RESET}")
    else if(hashEntry._1.endsWith("00000]")) print(s"\r${Console.YELLOW}current progress: ${Console.BLUE}${hashEntry._1}${Console.RESET} --> ${Console.BLUE}${bytesToHexString(hashEntry._2)}${Console.RESET}")
  }

  def createQuietSink(challenge: HChallenge): Sink[(String, Array[Byte]), Future[Done]] = Sink.foreach{ (hashEntry: (String, Array[Byte])) =>
    if(rightHash(challenge.targetHash)(hashEntry)) println(s"\r${Console.GREEN} * FOUND * ${Console.CYAN}${hashEntry._1}${Console.RESET} --> ${Console.CYAN}${bytesToHexString(hashEntry._2)}${Console.RESET}")
  }

  def createSimpleScanGraph[Mat1, Mat2](source: Source[(String, Array[Byte]), Mat1], sink: Sink[(String, Array[Byte]), Mat2]): Graph[ClosedShape.type, Mat2] = {
    import GraphDSL.Implicits._
    GraphDSL.create(sink) { implicit builder: GraphDSL.Builder[Mat2] => graphSink =>
      source ~> graphSink
      ClosedShape
    }
  }

  def createParallelScanGraph[Mat2](challenge: HChallenge, par: Int, sink: Sink[(String, Array[Byte]), Mat2]) = {
    //TODO 3.4: create a graph with a source, parallel flows, and a sink. Don't forget that "par" argument is the number of parallel flows. Don't forget it must stop when the tarhet hash is found.
    ???
  }

  def runSimpleScan(challenge: HChallenge)(implicit matzr: Materializer): Future[Done] = {
    challenge.simpleScan.runWith(createProgressSink(challenge))
  }

  def runSimpleScanWithGraph(challenge: HChallenge)(implicit matzr: Materializer): Future[Done] = {
    RunnableGraph.fromGraph(createSimpleScanGraph(challenge.simpleScan, createProgressSink(challenge))).run()
  }

  def runParallelScanWithGraph(challenge: HChallenge, par: Int)(implicit matzr: Materializer): Future[Done] = {
    //TODO 3.5: create graph with the requested level of parallelism and tun it with the "progress sink"
    ???
  }

  def runParallelScanWithGraph(challenge: HChallenge, par: Int, sink: Sink[(String, Array[Byte]), Future[Done]])(implicit matzr: Materializer): Future[Done] = {
    //TODO 3.5 (Optional): create graph with the requested level of parallelism and tun it with the specified sink
    ???
  }


  object GraphElements {
    import HChallenge.hashEntry

    def hashFlow: Flow[Int, (String, Array[Byte]), NotUsed] = {
        //TODO 3.1: create a flow that take an Int as input and ouputs (String, Array[Byte]) where the string is the hash input and the Array is the hash value.
        ???
    }

    def source(range: Range): Source[Int, NotUsed] = ??? //TODO 3.2 create a source of numbers. DEAD simple, don't look for something complicated ;)

    def parallelHashFlow(parts: Int): Flow[Int, (String, Array[Byte]), NotUsed] = {
      /* TODO 3.3 Create an open graph that connect the parallel flows :
          - create balance using as much outputs as specified by "parts" argument
          - create a merge using as much inputs as balance outputs
          - connect balance, flow and merge. Don't forget that this must be done as much time as...
          - return the right shape (not a closed shape)
       */
      ???
    }

  }
}

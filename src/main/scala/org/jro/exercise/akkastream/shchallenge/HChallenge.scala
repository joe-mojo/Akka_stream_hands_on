package org.jro.exercise.akkastream.shchallenge

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

  def cutIn(parts: Int): Seq[HChallenge] = {
    val maxParts = Math.min(parts, inputRange.size)
    val partSize = inputRange.size / maxParts
    val remainingSize = inputRange.size % maxParts
    @tailrec
    def splitRange(rangeTail: Range, remainder: Int, result: List[Range]): List[Range] = {
      if(rangeTail.size > partSize) {
        val bonus = if(remainder > 0) 1 else 0
        val ranges = rangeTail.splitAt(partSize + bonus)
        splitRange(ranges._2, remainder - bonus, result :+ ranges._1)
      } else {
        result :+ rangeTail
      }
    }
    splitRange(inputRange, remainingSize,  List.empty[Range]).map(r => copy(inputRange = r))
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
    import GraphDSL.Implicits._
    GraphDSL.create(sink) { implicit builder: GraphDSL.Builder[Mat2] => graphSink =>
      GraphElements.source(challenge.inputRange) ~> GraphElements.parallelHashFlow(par).takeWhile(wrongHash(challenge.targetHash), inclusive = true) ~> graphSink
      ClosedShape
    }
  }

  def runSimpleScan(challenge: HChallenge)(implicit matzr: Materializer): Future[Done] = {
    challenge.simpleScan.runWith(createProgressSink(challenge))
  }

  def runSimpleScanWithGraph(challenge: HChallenge)(implicit matzr: Materializer): Future[Done] = {
    RunnableGraph.fromGraph(createSimpleScanGraph(challenge.simpleScan, createProgressSink(challenge))).run()
  }

  def runParallelScanWithGraph(challenge: HChallenge, par: Int)(implicit matzr: Materializer): Future[Done] = {
    RunnableGraph.fromGraph(createParallelScanGraph(challenge, par, createProgressSink(challenge))).run()
  }

  def runParallelScanWithGraph(challenge: HChallenge, par: Int, sink: Sink[(String, Array[Byte]), Future[Done]])(implicit matzr: Materializer): Future[Done] = {
    RunnableGraph.fromGraph(createParallelScanGraph(challenge, par, sink)).run()
  }


  object GraphElements {
    import HChallenge.hashEntry

    def hashFlow: Flow[Int, (String, Array[Byte]), NotUsed] = Flow.fromFunction(wrap _ andThen hashEntry)

    def source(range: Range): Source[Int, NotUsed] = Source(range)

    def parallelHashFlow(parts: Int): Flow[Int, (String, Array[Byte]), NotUsed] = Flow.fromGraph(GraphDSL.create() { implicit builder ⇒
      import GraphDSL.Implicits._
      val dispatIntegers = builder.add(Balance[Int](parts, waitForAllDownstreams = false))
      val mergeHashEntries = builder.add(Merge[(String, Array[Byte])](parts))
      for(p <- 0 until parts ) {
        dispatIntegers.out(p) ~> hashFlow.async ~> mergeHashEntries.in(p)
      }
      FlowShape(dispatIntegers.in, mergeHashEntries.out)
    })

  }
}

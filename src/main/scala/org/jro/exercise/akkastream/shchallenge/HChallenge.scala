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
    /* TODO 1.1 create a source of Int mapped to (String, Array[Byte]) where the string is the hash input and the Array is the hash value. Don't forget it must stop when the tarhet hash is found.
       Hint : use function in Utils._
     */
    ???
  }

}

object HChallenge {
  def wrongHash(targetHash: Array[Byte])(hashEntry: (String, Array[Byte])): Boolean = ! rightHash(targetHash)(hashEntry)
  def rightHash(targetHash: Array[Byte])(hashEntry: (String, Array[Byte])): Boolean = hashEntry._2 sameElements targetHash
  def hashEntry(input: String): (String, Array[Byte]) = (input, md5sum(input))
}


object HChallengeBuilder {
  def createProgressSink(challenge: HChallenge): Sink[(String, Array[Byte]), Future[Done]] = {
    //TODO 1.2: create a sink to display progress and result
    ???
  }

  def createQuietSink(challenge: HChallenge): Sink[(String, Array[Byte]), Future[Done]] = {
    //TODO 1.2 (optional): create a sink to display result only
    ???
  }

  def createSimpleScanGraph[Mat1, Mat2](source: Source[(String, Array[Byte]), Mat1], sink: Sink[(String, Array[Byte]), Mat2]): Graph[ClosedShape.type, Mat2] = {
    /* TODO 2.1: create a simple  with GraphDSL that do exactly the same as HChallenge.simpleScan completed at 1.1
       Hint: you need only to connect the source and the sink
     */
    ???
  }

  def createParallelScanGraph[Mat2](challenge: HChallenge, par: Int, sink: Sink[(String, Array[Byte]), Mat2]) = {
    //TODO 3.4: create a graph with a source, parallel flows, and a sink. Don't forget that "par" argument is the number of parallel flows. Don't forget it must stop when the tarhet hash is found.
    ???
  }

  def runSimpleScan(challenge: HChallenge)(implicit matzr: Materializer): Future[Done] = {
    //TODO 1.3: create a simple scan and run it using the sinke created at 1.2
    ???
  }

  def runSimpleScanWithGraph(challenge: HChallenge)(implicit matzr: Materializer): Future[Done] = {
    //TODO 2.2: create a source, a sink and pass them to createSimpleScanGraph in order to create the graph, then run it
    ???
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

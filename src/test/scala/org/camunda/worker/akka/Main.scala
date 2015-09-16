package org.camunda.worker.akka

import scala.collection.JavaConversions._
import akka.actor._
import org.camunda.worker.akka.worker._
import scala.io.StdIn._
import org.camunda.worker.akka.PollActor.Poll
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._

/**
 * @author Philipp Ossler
 */
// example app as template
object Main extends App {
 
  println("starting...........")
  println("press ENTER to exit")
  println("===================")
  println("")
  
  // create actor system
  val system = ActorSystem("MyActorSystem")
  
  // create worker
  val worker = system.actorOf(UnreliableWorker.props(delay = 200, reliability = 0.75), name = "worker-1")
  val worker2 = system.actorOf(SimpleWorker.props(delay = 100), name = "worker-2")
  val worker3 = system.actorOf(SimpleWorker.props(delay = 100), name = "worker-3")
  
  // start polling
  val pollActor = system.actorOf(PollActor.props(hostAddress = "http://localhost:8080/engine-rest", maxTasks = 5, waitTime= 100, lockTime = 600), name = "poller")
  pollActor ! Poll(topicName = "reserveOrderItems", worker)
  pollActor ! Poll(topicName = "payment", worker2)
  pollActor ! Poll(topicName = "shipment", worker3)
  
  // waiting for end
  val input = readLine()
  println("")
  println("===================")
  println("shutting down......")
  
  system.shutdown
  system.awaitTermination()
  println("done")
  
  System.exit(0)
}
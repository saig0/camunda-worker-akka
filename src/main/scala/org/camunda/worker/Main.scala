

package org.camunda.worker/**
 * @author Philipp Ossler
 */

import scala.collection.JavaConversions._
import org.camunda.worker.dto.LockedTaskDto
import akka.actor.ActorSystem
import akka.actor.{Actor, ActorRef, ActorLogging, Props}

object Main extends App {
 
  println("started...........")
  
  val system = ActorSystem("MyActorSystem")
  
  val worker = system.actorOf(Props[SimpleWorker], name = "worker-1")
  worker ! "init"
  
  val poller = system.actorOf(PollActor.props(hostAddress = "http://localhost:8080/engine-rest"), name = "poller")
  poller ! PollActor.Poll(topicName = "reserveOrderItems", worker)
  
  
  // waiting a bit and then exit
  java.lang.Thread.sleep(10000)
  system.shutdown
}
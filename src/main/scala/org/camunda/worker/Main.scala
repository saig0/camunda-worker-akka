

package org.camunda.worker/**
 * @author Philipp Ossler
 */

import scala.collection.JavaConversions._
import org.camunda.worker.dto.LockedTaskDto
import akka.actor.ActorSystem
import akka.actor.{Actor, ActorRef, ActorLogging, Props}

import org.camunda.worker.PollActor.Poll

object Main extends App {
 
  // create actor system
  val system = ActorSystem("MyActorSystem")
  
  // create worker
  val worker = system.actorOf(Props[SimpleWorker], name = "worker-1")
  val worker2 = system.actorOf(Props[SimpleWorker], name = "worker-2")
  
  // start polling
  val pollActor = system.actorOf(PollActor.props(hostAddress = "http://localhost:8080/engine-rest"), name = "poller")
  pollActor ! Poll(topicName = "reserveOrderItems", worker)
  pollActor ! Poll(topicName = "payment", worker2)
  
  // TODO heart beat
  
  // waiting a bit and then exit
  java.lang.Thread.sleep(10000)
  system.shutdown
}
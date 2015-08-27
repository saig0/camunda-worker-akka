

package org.camunda.worker.akka/**
 * @author Philipp Ossler
 */

import scala.collection.JavaConversions._
import org.camunda.worker.akka.PollActor.Poll
import org.camunda.worker.dto.LockedTaskDto
import akka.actor._
import org.camunda.worker.akka.worker._

object Main extends App {
 
  // create actor system
  val system = ActorSystem("MyActorSystem")
  
  // create worker
  val worker = system.actorOf(Props[UnreliableWorker], name = "worker-1")
  val worker2 = system.actorOf(Props[SimpleWorker], name = "worker-2")
  
  // start polling
  val pollActor = system.actorOf(PollActor.props(hostAddress = "http://localhost:8080/engine-rest", maxTasks = 5), name = "poller")
  pollActor ! Poll(topicName = "reserveOrderItems", worker)
  pollActor ! Poll(topicName = "payment", worker2)
  
  // TODO heart beat
  
  // waiting a bit and then exit
  java.lang.Thread.sleep(30000)
  system.shutdown
}
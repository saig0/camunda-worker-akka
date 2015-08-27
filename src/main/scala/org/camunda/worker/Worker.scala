package org.camunda.worker

/**
 * @author Philipp Ossler
 */

import akka.actor.{Actor, ActorLogging, Props}
import org.camunda.worker.dto._
import org.camunda.worker.PollActor._

trait Worker extends Actor with ActorLogging {
  
  def receive = {
    case task: LockedTaskDto => 
      
      log.info(s"execute task '$task'")  
      
      work(task)
      
      sender ! Complete(task.getId)
  }
  
  def work(task: LockedTaskDto)
}
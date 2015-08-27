package org.camunda.worker.akka.worker

import org.camunda.worker.dto._
import org.camunda.worker.akka.PollActor._
import java.lang.Exception
import akka.actor.{Actor, ActorRef, ActorLogging, Props}

trait Worker extends Actor with ActorLogging {
  
  def receive = {
    case task: LockedTaskDto => 
      
      log.info(s"execute task '$task'")  
      
      try {
       work(task)
       
       sender ! Complete(task.getId)
      } catch {
        case e: Exception => {
          log.error("failed task: {}", e)
          
          sender ! FailedTask(task.getId, e.getMessage)
        }
      }
      
  }
  
  def work(task: LockedTaskDto)
}
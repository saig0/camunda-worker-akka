package org.camunda.worker.akka.worker

import org.camunda.worker.akka.PollActor._
import java.lang.Exception
import akka.actor.{Actor, ActorRef, ActorLogging, Props}
import org.camunda.worker.akka.client.LockedTask

trait Worker extends Actor with ActorLogging {
    
  def receive = {
    case task: LockedTask => 
      
      log.info(s"execute task '$task'")  
      
      try {
       work(task)
       
       sender ! Complete(task.id)
      } catch {
        case e: Exception => {
          log.error("failed task: {}", e)
          
          sender ! FailedTask(task.id, e.getMessage)
        }
      }
      
  }
  
  def work(task: LockedTask)
}
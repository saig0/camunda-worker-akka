package org.camunda.worker.akka.worker

import org.camunda.worker.akka.PollActor._
import java.lang.Exception
import akka.actor.{Actor, ActorRef, ActorLogging, Props}
import org.camunda.worker.akka.client.LockedTask
import org.camunda.worker.akka.client.VariableValue
import scala.concurrent.Future
import scala.util.{Success, Failure}
import scala.concurrent._
import ExecutionContext.Implicits.global

trait Worker extends Actor with ActorLogging {
    
  val consumerId: String = getNameOfActor
  
  def receive = {
    case task: LockedTask => 
      
      log.debug(s"execute task '$task'")  
      val manager = sender
      
      work(task) onComplete {
         case Success(variables)  => manager ! Complete(consumerId, task.id, variables)
         case Failure(cause)      => manager ! FailedTask(consumerId, task.id, cause.getMessage)
      }
  }
  
  def work(task: LockedTask): Future[Map[String, VariableValue]]
  
  private def getNameOfActor = {
    val name = self.path.name
    if(name.startsWith("$")) {
      // in case of a worker of router
      self.path.parent.name
    } else {
      name
    }
  }
}
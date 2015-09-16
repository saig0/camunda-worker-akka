package org.camunda.worker.akka.worker

import org.camunda.worker.akka.client.LockedTask
import org.camunda.worker.akka.PollActor._
import akka.routing._
import akka.actor.Props
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.camunda.worker.akka.client.VariableValue

class SimpleWorker(delay: Int) extends Worker {
  
  def work(task: LockedTask): Future[Map[String, VariableValue]] = {
    
    Future {
      // simulate working
      java.lang.Thread.sleep(delay)
      
      Map( "test" -> 123 )
    }
  }
  
}

object SimpleWorker {
  
  def props(delay: Int = 1000): Props = 
    Props(new SimpleWorker(delay))
}
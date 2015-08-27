package org.camunda.worker.akka.worker

import org.camunda.worker.dto._
import org.camunda.worker.akka.PollActor._
import akka.routing._
import akka.actor.Props

class SimpleWorker(delay: Int) extends Worker {
  
  def work(task: LockedTaskDto) {
    
    // simulate working
    java.lang.Thread.sleep(delay)
    
  }
  
}

object SimpleWorker {
  
  def props(delay: Int = 1000): Props = 
    Props(new SimpleWorker(delay))
}
package org.camunda.worker.akka.worker

import org.camunda.worker.dto._
import org.camunda.worker.akka.PollActor._

class SimpleWorker extends Worker {
  
  def work(task: LockedTaskDto) {
    
    // simulate working
    java.lang.Thread.sleep(100)
    
  }
  
}
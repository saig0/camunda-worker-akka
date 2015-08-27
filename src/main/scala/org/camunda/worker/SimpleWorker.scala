package org.camunda.worker

/**
 * @author Philipp Ossler
 */

import akka.actor.{Actor, ActorLogging, Props}
import org.camunda.worker.dto._
import org.camunda.worker.PollActor._

class SimpleWorker extends Worker {
  
  def work(task: LockedTaskDto) {
    
    // simulate working
    java.lang.Thread.sleep(100)
    
  }
  
}
package org.camunda.worker

/**
 * @author Philipp Ossler
 */

import akka.actor.{Actor, ActorLogging, Props}
import org.camunda.worker.dto.LockedTaskDto

class SimpleWorker extends Actor with ActorLogging {
  
  import context._
  
  def receive = {
    case task: LockedTaskDto => 
      log.info(s"execute $task")
  }
}
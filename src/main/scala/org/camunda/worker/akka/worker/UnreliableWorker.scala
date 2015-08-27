package org.camunda.worker.akka.worker

/**
 * @author Philipp Ossler
 */

import org.camunda.worker.dto._
import scala.util.Random

class UnreliableWorker extends Worker {

  def work(task: LockedTaskDto) {
    
    // simulate working
    java.lang.Thread.sleep(100)
    
    val failing = Random.nextDouble() < 0.25
    if(failing) {
      throw new RuntimeException("failing task")
    }
    
  }
  
}
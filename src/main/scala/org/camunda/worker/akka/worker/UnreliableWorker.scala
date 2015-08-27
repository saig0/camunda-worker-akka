package org.camunda.worker.akka.worker

/**
 * @author Philipp Ossler
 */

import org.camunda.worker.dto._
import scala.util.Random
import akka.routing._
import akka.actor.Props

class UnreliableWorker(delay: Int, reliability: Double) extends Worker {

  def work(task: LockedTaskDto) {
    
    // simulate working
    java.lang.Thread.sleep(delay)
    
    val failing = Random.nextDouble() > reliability
    if(failing) {
      throw new RuntimeException("unreliable task")
    }
    
  }
  
}

object UnreliableWorker {
  
  def props(delay: Int = 1000, reliability: Double = 0.75): Props = 
    FromConfig.props(Props(new UnreliableWorker(delay, reliability)))
}
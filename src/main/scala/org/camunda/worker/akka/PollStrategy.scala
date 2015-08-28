package org.camunda.worker.akka

/**
 * @author Philipp Ossler
 */

import org.camunda.worker.dto._
import scala.math.{min, max, round}
import scala.concurrent._
import ExecutionContext.Implicits.global
import org.slf4j.LoggerFactory

class PollStrategy(waitTime: Int = 1000) {
  
  import PollStrategy._
  
  var currentWaitTime = waitTime
  
  def poll(topicName: String, polling : () => LockedTasksResponseDto, handler: LockedTasksResponseDto => Unit, sheduler: () => Unit) {
    try {
      val response = polling()
      
      val taskCount = response.getTasks.size
        
        if(taskCount == 0) {
          currentWaitTime = min( toInt(currentWaitTime * factorEmptyResponse), maxWaitTime)
        } else {
          currentWaitTime = max( toInt(currentWaitTime * factorNonEmptyResponse), waitTime)
        }
      
      handler(response)
        
    } catch {
        case e: Exception => 
          currentWaitTime = min( toInt(currentWaitTime * factorFailure), maxWaitTime)
          
          val errorMessage = e.getMessage
          logger.error(s"failure: $errorMessage")
      }
    
    Future { 
        logger.info(s"wait '$currentWaitTime' till next polling for '$topicName'")
        java.lang.Thread.sleep(currentWaitTime)
    } onComplete  { _ => sheduler() }
  }
  
 private def toInt(d: Double): Int = round(d.toFloat)
  
}

object PollStrategy {
  
  val maxWaitTime = 30000;
  
  val factorEmptyResponse: Double = 1.5
  val factorNonEmptyResponse: Double = 0.25
  val factorFailure: Double = 2.5
  
  private lazy val logger = LoggerFactory.getLogger(this.getClass())
  
}
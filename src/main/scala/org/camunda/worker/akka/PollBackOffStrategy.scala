package org.camunda.worker.akka

import scala.concurrent.duration._
import scala.math.{min, max, round}

/**
 * @author Philipp Ossler
 */
class PollBackOffStrategy(initialWaitTime: Duration = 1000 millis) {
  
  import PollBackOffStrategy._
  
  var currentWaitTime: Duration = initialWaitTime
  
  def waitTime: FiniteDuration = currentWaitTime.copy()
  
  def noPolledTasks {
    val waitTime = toInt(currentWaitTime.toMillis * factorEmptyResponse)
    currentWaitTime = min(waitTime, maxWaitTime.toMillis) millis
  }
  
  def polledTasks {
    val waitTime = toInt(currentWaitTime.toMillis * factorNonEmptyResponse)
    currentWaitTime = max(waitTime, initialWaitTime.toMillis) millis
  }
  
  def failedToPollTasks {
    val waitTime = toInt(currentWaitTime.toMillis * factorFailure)
    currentWaitTime = min(waitTime, maxWaitTime.toMillis) millis
  }
  
 private def toInt(d: Double): Int = round(d.toFloat)
  
}

object PollBackOffStrategy {
  
  val maxWaitTime = 30000 millis;
  
  val factorEmptyResponse: Double = 1.5
  val factorNonEmptyResponse: Double = 0.25
  val factorFailure: Double = 2.5
  
}
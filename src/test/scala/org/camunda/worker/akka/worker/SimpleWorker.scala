package org.camunda.worker.akka.worker

import org.camunda.worker.akka.client.LockedTask
import org.camunda.worker.akka.PollActor._
import akka.routing._
import akka.actor.Props
import org.camunda.worker.akka.client.VariableValue
import org.camunda.worker.akka.Worker

class SimpleWorker(delay: Int) extends Worker {

  def work(task: LockedTask): Map[String, VariableValue] = {
    
    val variableName: String = task.variable[String]("var") getOrElse "test"

    // simulate working
    java.lang.Thread.sleep(delay)

    log.info("worker is working")

    Map(variableName -> 123)
  }

}

object SimpleWorker {

  def props(delay: Int = 1000): Props =
    Props(new SimpleWorker(delay))
}
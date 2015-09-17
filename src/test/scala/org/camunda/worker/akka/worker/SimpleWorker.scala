package org.camunda.worker.akka.worker

import org.camunda.worker.akka.client.LockedTask
import org.camunda.worker.akka.PollActor._
import akka.routing._
import akka.actor.Props
import org.camunda.worker.akka.client.VariableValue
import org.camunda.worker.akka.client.VariableValue.anyToVariableValue
import org.camunda.worker.akka.Worker

class SimpleWorker(delay: Int) extends Worker {

  def work(task: LockedTask): Map[String, VariableValue] = {
    
    val variableName = task.variables.get("var") match {
      case Some(variableValue)  => variableValue.toValue[String]
      case None                 => "test"
    }
    

    // simulate working
    java.lang.Thread.sleep(delay)

    Map(variableName -> 123)
  }

}

object SimpleWorker {

  def props(delay: Int = 1000): Props =
    Props(new SimpleWorker(delay))
}
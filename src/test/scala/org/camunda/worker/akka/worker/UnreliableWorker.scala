package org.camunda.worker.akka.worker

import org.camunda.worker.akka.client.LockedTask
import scala.util.Random
import akka.routing._
import akka.actor.Props
import org.camunda.worker.akka.client.VariableValue
import org.camunda.worker.akka.Worker

class UnreliableWorker(delay: Int, reliability: Double) extends Worker {

  def work(task: LockedTask): Map[String, VariableValue] = {

    // simulate working
    java.lang.Thread.sleep(delay)

    val failing = Random.nextDouble() > reliability
    if (failing) {
      throw new RuntimeException("unreliable task")
    } else {
      Map()
    }
  }

}

object UnreliableWorker {

  def props(delay: Int = 1000, reliability: Double = 0.75): Props =
    FromConfig.props(Props(new UnreliableWorker(delay, reliability)))
}
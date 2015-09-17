package org.camunda.worker.akka

import org.camunda.worker.akka.PollActor._
import akka.actor.{ Actor, ActorRef, ActorLogging }
import org.camunda.worker.akka.client.LockedTask
import org.camunda.worker.akka.client.VariableValue
import scala.concurrent.Future
import scala.util.{ Success, Failure }
import scala.concurrent._
import ExecutionContext.Implicits.global
import akka.actor.actorRef2Scala

/**
 * Worker that can scheduled by PollActor.
 */
trait Worker extends Actor with ActorLogging {

  // consumerId should be the same as for poll&lock request
  val consumerId: String = Worker.getNameOfActor(self)

  def receive = {
    case task: LockedTask =>

      log.debug(s"execute task '$task'")

      try {
        // execute the task
        val result = work(task)
        sender ! Complete(consumerId, task.id, result)
      } catch {
        case cause: Exception => {
          log.error(cause, "failed to execute task $taskId")
          sender ! FailedTask(consumerId, task.id, cause.getMessage)
        }
      }
  }

  // should implement by the concrete worker
  def work(task: LockedTask): Map[String, VariableValue]
  
}

object Worker {

  def getNameOfActor(actor: ActorRef): String = {
    val name = actor.path.name
    if (name.startsWith("$")) {
      // in case of a worker of router
      actor.path.parent.name
    } else {
      name
    }
  }
}
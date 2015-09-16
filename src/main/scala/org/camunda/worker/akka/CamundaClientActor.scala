package org.camunda.worker.akka

import akka.actor.{Actor, ActorLogging, Props}
import scala.util.{Success, Failure}
import scala.concurrent.ExecutionContext.Implicits.global
import org.camunda.worker.akka.PollActor.{FailedToPollTasks, LockedTasks}
import akka.actor.actorRef2Scala
import org.camunda.worker.akka.client.CamundaClient
import org.camunda.worker.akka.client.CompleteTaskRequest
import org.camunda.worker.akka.client.FailedTaskRequest
import org.camunda.worker.akka.client.PollAndLockTaskRequest

/**
 * Actor what call the camunda Rest Client and handle the response.
 * 
 * @author Philipp Ossler
 */
class CamundaClientActor(hostAddress: String) extends Actor with ActorLogging {
 
  import CamundaClientActor._
  
  val client = new CamundaClient(hostAddress)
  
  def receive = {
    case PollRequest(request)            => {
      val requestActor = sender
      client.pollTasks(request) onComplete {
          case Success(response) => {
                                      log.debug(s"polled '${response.tasks.size}' tasks from server for topic '${request.topicName}'")
                                      requestActor ! LockedTasks(response.tasks)
                                    }
          case Failure(t)        => {
                                      log.error(t, s"failed to poll tasks from server for topic '${request.topicName}'")
                                      requestActor ! FailedToPollTasks(t)
                                     }
      }
    }
    case TaskCompleted(taskId, request)  => {
      client.taskCompleted(taskId, request) onComplete {
        case Success(_)  => log.debug(s"server acknowledged that task '$taskId' is completed")
        case Failure(t)  => log.error(t, s"failed to acknowledge that task '$taskId' is completed")
      }
    }
    case TaskFailed(taskId, request)    => {
      client.taskFailed(taskId, request) onComplete {
        case Success(_)  => log.debug(s"server acknowledge that task '${taskId}' is failed")
        case Failure(t)  => log.error(t, s"failed to acknowledge that task '$taskId' is failed")
      }
    }
  }
  
}

object CamundaClientActor {
  
  def props(hostAddress: String): Props = Props(new CamundaClientActor(hostAddress))
  
  case class PollRequest(request: PollAndLockTaskRequest)
  
  case class TaskCompleted(taskId: String, request: CompleteTaskRequest)
  
  case class TaskFailed(taskId: String, request: FailedTaskRequest)
    
}
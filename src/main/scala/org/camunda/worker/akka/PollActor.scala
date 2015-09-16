package org.camunda.worker.akka

/**
 * @author Philipp Ossler
 */

import akka.actor.{Actor, ActorRef, ActorLogging, Props}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import org.springframework.web.client.RestTemplate
import org.camunda.worker.dto._
import scala.math.{min, max, round}
import scala.collection.JavaConversions.{seqAsJavaList, asScalaBuffer}
import scala.concurrent._
import org.camunda.worker.akka.client.CamundaClientActor.{PollRequest, TaskCompleted, TaskFailed}
import org.camunda.worker.akka.client.LockedTask
import org.camunda.worker.akka.client.CamundaClientActor
import org.camunda.worker.akka.client.CamundaClientActor.PollRequest
import org.camunda.worker.akka.client.PollAndLockTaskRequest
import scala.util.{Success, Failure}
import org.camunda.worker.akka.client.CamundaClientActor.TaskCompleted
import org.camunda.worker.akka.client.CompleteTaskRequest
import org.camunda.worker.akka.client.CamundaClientActor.TaskFailed
import org.camunda.worker.akka.client.CamundaClientActor.TaskFailed
import org.camunda.worker.akka.client.FailedTaskRequest
import akka.actor.PoisonPill
import akka.actor.Kill
import scala.util.Either
import org.camunda.worker.akka.client.VariableValue

class PollActor(hostAddress: String, maxTasks: Int, lockTime: Int, waitTime: Int) extends Actor with ActorLogging {
  
  import PollActor._
  import context._
    
  val clientActor = context.actorOf(CamundaClientActor.props(hostAddress), name = "client")
  
  implicit val timeout = Timeout(10 seconds)
  
  def receive = {
    case poll @ Poll(topicName, worker, variableNames, strategy) => {
      log.info(s"poll tasks from server '$hostAddress' with topic '$topicName'")
      
      val result = clientActor ? PollRequest(request = PollAndLockTaskRequest(topicName, "demo", lockTime, maxTasks, variableNames))
      result onComplete {
        case Success(LockedTasks(tasks))  => {
                                                if(!tasks.isEmpty) {
                                                  log.debug(s"shedule '${tasks.size}' tasks for topic '${topicName}'")
                                                  tasks.foreach( task => worker ! task )
                                                } else {
                                                  log.debug(s"no tasks polled for topic '${topicName}'")
                                                  // wait more time
                                                }
                                                // set wait time
                                                system.scheduler.scheduleOnce(1000 millis, self, poll)
                                              }
        case Success(FailedToPollTasks(cause))  => {
                                                     log.error(cause,"poll failed") // wait
                                                     system.scheduler.scheduleOnce(5000 millis, self, poll)
                                                   }
        case Success(result)                    => log.warning(s"unknown result: ${result}")
        case Failure(cause)                     => {
                                                     log.error(cause,"failure while poll") // wait
                                                     system.scheduler.scheduleOnce(10000 millis, self, poll)
                                                   }
      }
    }
    
    case Complete(taskId, variables) => {
      
      val worker = getNameOfActor(sender)      
      log.info(s"task '$taskId' completed by '$worker'")
      
      clientActor ! TaskCompleted(taskId, CompleteTaskRequest(consumerId = "demo", variables))
    }      
      
    case FailedTask(taskId, errorMessage) => {
        
      val worker = getNameOfActor(sender)
      log.info(s"task '$taskId' failed by '$worker'")
      
      clientActor ! TaskFailed(taskId, FailedTaskRequest(consumerId = "demo", errorMessage))
    }
  }
  
  private def toInt(d: Double): Int = round(d.toFloat)
  
  private def getNameOfActor(actor: ActorRef) = {
    val name = actor.path.name
    if(name.startsWith("$")) {
      // in case of a worker of router
      actor.path.parent.name
    } else {
      name
    }
  }
  
}

object PollActor {
  
  def props(hostAddress: String, maxTasks: Int = 1, lockTime: Int = 60, waitTime: Int = 3000): Props = 
    Props(new PollActor(hostAddress, maxTasks, lockTime, waitTime))
  
  case class Poll(topicName: String, worker: ActorRef, variableNames: List[String] = List(), strategy: PollStrategy = new PollStrategy())
  
  case class Complete(taskId: String, variables: Map[String, VariableValue] = Map())

  case class FailedTask(taskId: String, errorMessage: String)
  
  case class LockedTasks(tasks: List[LockedTask])
  
  case class FailedToPollTasks(cause: Throwable)
}
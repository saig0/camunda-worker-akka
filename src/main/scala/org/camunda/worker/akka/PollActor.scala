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
  
  // timeout for client actor
  implicit val timeout = Timeout(5000 millis)
  
  def receive = {
    case poll @ Poll(topicName, worker, variableNames, strategy) => {
      log.debug(s"poll tasks from server '$hostAddress' with topic '$topicName'")
      
      // poll tasks from server
      val result = clientActor ? PollRequest(request = PollAndLockTaskRequest(topicName, "demo", lockTime, maxTasks, variableNames))
      result onComplete {
        case Success(LockedTasks(tasks))        => {
                                                    if(!tasks.isEmpty) {
                                                      log.info(s"shedule '${tasks.size}' tasks for topic '$topicName'")
                                                      // assigne tasks to worker
                                                      tasks.foreach( task => worker ! task )
                                                      // calculate wait time
                                                      strategy.polledTasks
                                                    } else {
                                                      log.debug(s"no tasks polled for topic '$topicName'")
                                                      // calculate wait time
                                                      strategy.noPolledTasks
                                                    }
                                                    log.debug(s"wait '${strategy.waitTime}ms' till poll for topic '$topicName'")
                                                    // schedule next poll request 
                                                    system.scheduler.scheduleOnce(strategy.waitTime, self, poll)
                                                   }
        case Success(FailedToPollTasks(cause))  => {
                                                     log.error(cause,s"failed to poll tasks for topic '$topicName'")
                                                     // calculate wait time
                                                     strategy.failedToPollTasks
                                                     // schedule next poll request 
                                                     system.scheduler.scheduleOnce(strategy.waitTime, self, poll)
                                                   }
        case Success(result)                    =>   log.warning(s"unknown result from client actor: $result")
        case Failure(cause)                     => {
                                                     log.error(cause,s"failed to poll tasks for topic '$topicName'")
                                                     // calculate wait time
                                                     strategy.failedToPollTasks
                                                     // schedule next poll request 
                                                     system.scheduler.scheduleOnce(strategy.waitTime, self, poll)
                                                    }
      }
    }
    
    case Complete(consumerId, taskId, variables) => {
      log.info(s"task '$taskId' completed by '$consumerId'")
      // notify server that task is completed
      clientActor ! TaskCompleted(taskId, CompleteTaskRequest("demo", variables))
    }      
      
    case FailedTask(consumerId, taskId, errorMessage) => {
      log.info(s"task '$taskId' failed by '$consumerId'")
      // notify server that task is failed
      clientActor ! TaskFailed(taskId, FailedTaskRequest("demo", errorMessage))
    }
  }
  
  private def toInt(d: Double): Int = round(d.toFloat)
  
}

object PollActor {
  
  def props(hostAddress: String, maxTasks: Int = 1, lockTime: Int = 60, waitTime: Int = 3000): Props = 
    Props(new PollActor(hostAddress, maxTasks, lockTime, waitTime))
  
  case class Poll(topicName: String, worker: ActorRef, variableNames: List[String] = List(), strategy: PollBackOffStrategy = new PollBackOffStrategy())
  
  case class Complete(consumerId: String, taskId: String, variables: Map[String, VariableValue] = Map())

  case class FailedTask(consumerId: String, taskId: String, errorMessage: String)
  
  case class LockedTasks(tasks: List[LockedTask])
  
  case class FailedToPollTasks(cause: Throwable)
}
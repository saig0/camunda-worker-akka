package org.camunda.worker.akka

import akka.actor.{ Actor, ActorRef, ActorLogging, Props }
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.util.{ Success, Failure }
import org.camunda.worker.akka.CamundaClientActor.{ PollRequest, TaskCompleted, TaskFailed }
import org.camunda.worker.akka.client._

/**
 * Coordinate polling of tasks.
 */
class PollActor(hostAddress: String, maxTasks: Int, lockTime: Int, waitTime: Int) extends Actor with ActorLogging {

  import PollActor._
  import context._

  // create actor for communication with camunda server
  val clientActor = context.actorOf(CamundaClientActor.props(hostAddress), name = "camunda-client")
  // 'ask' timeout for client actor
  implicit val timeout = Timeout(5 seconds)

  def receive = {

    case poll @ Poll(topicName, worker, variableNames, strategy) => {

      log.debug(s"poll tasks from server '$hostAddress' with topic '$topicName'")
      // use the name of the worker as consumerId
      val consumerId = Worker.getNameOfActor(worker)
      // poll tasks from server
      val response: Future[Any] = clientActor ? PollRequest(request = PollAndLockTaskRequest(topicName, consumerId, lockTime, maxTasks, variableNames))
      response onComplete {
        case Success(LockedTasks(tasks))       => handleSuccessfulPollRequest(poll, tasks)
        case Success(FailedToPollTasks(cause)) => handleFailedPollRequest(poll, cause)
        case Failure(cause)                    => handleFailedPollRequest(poll, cause)
        case Success(result)                   => log.error(s"unknown result from client actor: $result")
      }
    }

    case Complete(consumerId, taskId, variables) => {

      log.info(s"task '$taskId' completed by '$consumerId'")
      // notify the server that task is executed successful
      clientActor ! TaskCompleted(taskId, CompleteTaskRequest(consumerId, variables))
    }

    case FailedTask(consumerId, taskId, errorMessage) => {

      log.info(s"task '$taskId' failed by '$consumerId'")
      // notify the server that task is failed
      clientActor ! TaskFailed(taskId, FailedTaskRequest(consumerId, errorMessage))
    }
  }

  private def handleSuccessfulPollRequest(poll: Poll, tasks: List[LockedTask]) {
    val topicName = poll.topicName
    val worker = poll.worker
    val strategy = poll.strategy

    if (!tasks.isEmpty) {
      log.info(s"shedule '${tasks.size}' tasks for topic '$topicName'")
      // assign tasks to worker
      tasks.foreach(task => worker ! task)
      // calculate wait time
      strategy.polledTasks
    } else {
      log.debug(s"no tasks polled for topic '$topicName'")
      // calculate wait time
      strategy.noPolledTasks
    }

    schedulePollRequest(poll)
  }

  private def handleFailedPollRequest(poll: Poll, cause: Throwable) {
    log.error(cause, s"failed to poll tasks for topic '${poll.topicName}'")
    // calculate wait time
    poll.strategy.failedToPollTasks

    schedulePollRequest(poll)
  }

  private def schedulePollRequest(poll: Poll) {
    val waitTime = poll.strategy.waitTime

    log.debug(s"wait '${waitTime}ms' till poll for topic '${poll.topicName}'")
    // schedule next poll request 
    system.scheduler.scheduleOnce(poll.strategy.waitTime, self, poll)
  }

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
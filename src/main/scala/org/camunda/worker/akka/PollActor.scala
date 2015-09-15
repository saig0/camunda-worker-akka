package org.camunda.worker.akka

/**
 * @author Philipp Ossler
 */

import akka.actor.{Actor, ActorRef, ActorLogging, Props}
import org.springframework.web.client.RestTemplate
import org.camunda.worker.dto._
import scala.math.{min, max, round}

import scala.collection.JavaConversions.{seqAsJavaList, asScalaBuffer}
import scala.concurrent._
import org.camunda.worker.akka.client.CamundaClientActor.{PollRequest, TaskCompleted, TaskFailed}
import org.camunda.worker.akka.client.LockedTask

class PollActor(hostAddress: String, maxTasks: Int, lockTime: Int, waitTime: Int) extends Actor with ActorLogging {
  
  import PollActor._
  import context._
  
  val uri = s"$hostAddress/external-task/poll"
    
  def receive = {
    case poll @ Poll(topicName, worker, variableNames, strategy) => 
      log.info(s"start polling tasks on '$uri' with topic '$topicName'")
      
      strategy.poll(topicName, () => {
        pollTasks(topicName, getNameOfActor(worker), variableNames)
      }, response => {
        val taskCount = response.getTasks.size
        log.info(s"polled tasks for topic '$topicName': $taskCount")
        
        val tasks = (List[LockedTaskDto]() ++ response.getTasks)
        tasks.foreach( task => worker ! task )
      }, () => {
        self ! Poll(topicName, worker, variableNames, strategy)
      })
      
    case Complete(taskId, variables) => 
      
      val worker = getNameOfActor(sender)
      
      log.info(s"task '$taskId' completed by '$worker'")
      
      try {
        completeTask(taskId, worker, variables)
      } catch {
        case e: Exception => 
          val errorMessage = e.getMessage
          log.error(s"completing task failed: $errorMessage") 
      }
      
    case FailedTask(taskId, errorMessage) =>
        
      val worker = getNameOfActor(sender)
      log.info(s"task '$taskId' failed by '$worker'")
      
      try {
        failedTask(taskId, worker, errorMessage)
      } catch {
        case e: Exception => 
          val errorMessage = e.getMessage
          log.error(s"canceling task failed: $errorMessage") 
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
  
  private def pollTasks(topicName: String, consumerId: String, variableNames: List[String]): LockedTasksResponseDto = {
    
    // TODO use scala objects + json mapping
    
    val template = new RestTemplate()

    val request = new PollAndLockTaskRequestDto()
    request.setConsumerId(consumerId)
    request.setTopicName(topicName)
    request.setMaxTasks(maxTasks)
    request.setLockTimeInSeconds(lockTime)
    request.setVariableNames(variableNames)
    
    template.postForObject(uri, request, classOf[LockedTasksResponseDto])
  }
  
  private def completeTask(taskId: String, consumerId: String, variables: Map[String, Any]) {
    // TODO use scala objects + json mapping
    
    val template = new RestTemplate()
    
    val request = new CompleteTaskRequestDto()
    request.setConsumerId(consumerId)
    // request.setVariables(variables)
    
    template.postForObject(s"$hostAddress/external-task/$taskId/complete", request, classOf[Any])
  }
  
  private def failedTask(taskId: String, consumerId: String, errorMessage: String) {
    // TODO use scala objects + json mapping
    
    val template = new RestTemplate()
    
    val request = new FailedTaskRequestDto
    request.setConsumerId(consumerId)
    request.setErrorMessage(errorMessage)
    
    template.postForObject(s"$hostAddress/external-task/$taskId/failed", request, classOf[Any])
  }
  
}

object PollActor {
  
  def props(hostAddress: String, maxTasks: Int = 1, lockTime: Int = 60, waitTime: Int = 3000): Props = 
    Props(new PollActor(hostAddress, maxTasks, lockTime, waitTime))
  
  case class Poll(topicName: String, worker: ActorRef, variableNames: List[String] = List(), strategy: PollStrategy = new PollStrategy())
  
  case class Complete(taskId: String, variables: Map[String, Any] = Map())

  case class FailedTask(taskId: String, errorMessage: String)
  
  case class LockedTasks(tasks: List[LockedTask])
  
  case class FailedToPollTasks(cause: Throwable)
}
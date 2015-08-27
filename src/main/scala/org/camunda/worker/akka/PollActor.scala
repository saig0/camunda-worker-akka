package org.camunda.worker.akka

/**
 * @author Philipp Ossler
 */

import akka.actor.{Actor, ActorRef, ActorLogging, Props}
import org.springframework.web.client.RestTemplate
import org.camunda.worker.dto._

import scala.collection.JavaConversions._

import scala.concurrent._

class PollActor(hostAddress: String, maxTasks: Int, lockTime: Int, waitTime: Int) extends Actor with ActorLogging {
  
  import PollActor._
  import context._
  
  val uri = s"$hostAddress/external-task/poll"
  
  def receive = {
    case poll @ Poll(topicName, worker, variableNames) => 
      log.info(s"start polling tasks on '$uri' with topic '$topicName'")
      
      val response = pollTasks(topicName, getNameOfActor(worker), variableNames)
      
      val taskCount = response.getTasks.size
      log.info(s"polled tasks for topic '$topicName': $taskCount")
       
      val tasks = (List[LockedTaskDto]() ++ response.getTasks)
      tasks.foreach( task => worker ! task )
      
      Future { java.lang.Thread.sleep(waitTime) } onComplete  { _ => self ! poll}
      
    case Complete(taskId, variables) => 
      
      val worker = getNameOfActor(sender)
      
      log.info(s"task '$taskId' completed by '$worker'")
      
      completeTask(taskId, worker, variables)
      
    case FailedTask(taskId, errorMessage) =>
        
      val worker = getNameOfActor(sender)
      log.info(s"task '$taskId' failed by '$worker'")
      
      failedTask(taskId, worker, errorMessage)
  }
  
  private def getNameOfActor(actor: ActorRef) = actor.path.name
  
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
  
  case class Poll(topicName: String, worker: ActorRef, variableNames: List[String] = List())
  
  case class Complete(taskId: String, variables: Map[String, Any] = Map())

  case class FailedTask(taskId: String, errorMessage: String)
}
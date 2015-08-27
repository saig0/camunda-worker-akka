package org.camunda.worker

/**
 * @author Philipp Ossler
 */

import akka.actor.{Actor, ActorRef, ActorLogging, Props}
import org.springframework.web.client.RestTemplate
import org.camunda.worker.dto.PollAndLockTaskRequestDto
import org.camunda.worker.dto.LockedTasksResponseDto
import org.camunda.worker.dto.LockedTaskDto

import scala.collection.JavaConversions._

import scala.concurrent._

class PollActor(hostAddress: String, maxTasks: Int, lockTime: Int, waitTime: Int) extends Actor with ActorLogging {
  
  import PollActor._
  import context._
  
  val uri = s"$hostAddress/external-task/poll"
  
  def receive = {
    case poll @ Poll(topicName, worker, variableNames) => 
      log.info(s"start polling tasks on '$uri' with topic '$topicName'")
      
      val response = pollTasks(topicName, "akka", variableNames)
      
      val taskCount = response.getTasks.size
      log.info(s"polled tasks for topic '$topicName': $taskCount")
       
      val tasks = (List[LockedTaskDto]() ++ response.getTasks)
      tasks.foreach( task => worker ! task )
      
      Future { java.lang.Thread.sleep(waitTime) } onComplete  { _ => self ! poll}
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
  
}

object PollActor {
  
  def props(hostAddress: String, maxTasks: Int = 1, lockTime: Int = 60, waitTime: Int = 3000): Props = 
    Props(new PollActor(hostAddress, maxTasks, lockTime, waitTime))
  
  case class Poll(topicName: String, worker: ActorRef, variableNames: List[String] = List())
}
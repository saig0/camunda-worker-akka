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

class PollActor(hostAddress: String, maxTasks: Int, lockTime: Int) extends Actor with ActorLogging {
  
  import PollActor._
  import context._
  
  val uri = s"$hostAddress/external-task/poll"
  
  def receive = {
    case Poll(topicName, worker, variableNames) => 
      log.info(s"start polling tasks on $uri")
      
      val response = poll(topicName, "akka", variableNames)
      
      val taskCount = response.getTasks.size
      log.info(s"polled tasks: $taskCount")
      
      val tasks = (List[LockedTaskDto]() ++ response.getTasks)
      tasks.foreach( task => worker ! task )
  }
  
  private def poll(topicName: String, consumerId: String, variableNames: List[String]): LockedTasksResponseDto = {
    
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
  
  def props(hostAddress: String, maxTasks: Int = 1, lockTime: Int = 60): Props = 
    Props(new PollActor(hostAddress, maxTasks, lockTime))
  
  case class Poll(topicName: String, worker: ActorRef, variableNames: List[String] = List())
}
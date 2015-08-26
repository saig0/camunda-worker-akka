package org.camunda.worker

/**
 * @author Philipp Ossler
 */


import org.springframework.web.client.RestTemplate
import org.camunda.worker.dto.PollAndLockTaskRequestDto
import java.util.ArrayList
import org.camunda.worker.dto.LockedTasksResponseDto

import scala.collection.JavaConversions._

class Poller(hostAddress: String, maxTasks: Int = 1, lockTime: Int = 60) {
  
  val uri = s"$hostAddress/external-task"

  def poll(topicName: String, consumerId: String, variableNames: List[String] = List()): LockedTasksResponseDto = {
    
    // TODO use scala objects + json mapping
    
    val template = new RestTemplate()

    val request = new PollAndLockTaskRequestDto()
    request.setConsumerId(consumerId)
    request.setTopicName(topicName)
    request.setMaxTasks(maxTasks)
    request.setLockTimeInSeconds(lockTime)
    request.setVariableNames(variableNames)
    
    template.postForObject(s"$uri/poll", request, classOf[LockedTasksResponseDto])
  }
  
}
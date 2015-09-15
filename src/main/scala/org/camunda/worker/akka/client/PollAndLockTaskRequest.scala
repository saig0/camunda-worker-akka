package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class PollAndLockTaskRequest(
  topicName: String,
  consumerId: String,
  lockTimeInSeconds: Int,
  maxTasks: Int = 1,
  variableNames: List[String] = List()
)

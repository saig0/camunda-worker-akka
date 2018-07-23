package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class PollAndLockTaskRequest(
  workerId: String,
  maxTasks: Int = 1,
  topics: List[Topic] = List()
)

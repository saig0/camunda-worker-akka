package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class LockedTasksResponse(
  tasks: List[LockedTask]    
)
package org.camunda.worker.akka

/**
 * @author Philipp Ossler
 */

class PollResponse {
  var tasks: List[LockedTask] = List()
  
  override def toString() = s"lockedTasks: $tasks"
}

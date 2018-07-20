package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class CompleteTaskRequest(
  workerId: String,
  variables: Map[String,VariableValue] = Map()
)
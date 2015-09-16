package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class CompleteTaskRequest(
  consumerId: String,
  variables: Map[String,VariableValue] = Map()
)
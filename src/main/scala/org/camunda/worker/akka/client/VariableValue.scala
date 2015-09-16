package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class VariableValue(
  `type`: String,
  value: String,
  valueInfo: Map[String, Any] = Map()
)
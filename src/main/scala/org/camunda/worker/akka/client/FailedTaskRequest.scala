package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class FailedTaskRequest(
  consumerId: String,
  errorMessage: String
)
package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class LockedTask(
  id: String,
  topicName: String,
  lockTime: java.util.Date,
  activityId: String,
  activityInstanceId: String,
  processInstanceId: String,
  processDefinitionId: String,
  variables: Map[String, VariableValue]
)
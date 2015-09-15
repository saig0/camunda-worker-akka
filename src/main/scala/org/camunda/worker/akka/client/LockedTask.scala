package org.camunda.worker.akka.client

import java.util.{Date}

/**
 * @author Philipp Ossler
 */
case class LockedTask(
  id: String,
  topicName: String,
  lockTime: Date,
  activityId: String,
  activityInstanceId: String,
  processInstanceId: String,
  processDefinitionId: String,
  variables: Map[String, Any]
)
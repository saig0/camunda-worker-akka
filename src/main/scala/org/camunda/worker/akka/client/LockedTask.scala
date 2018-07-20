package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class LockedTask(
    id: String,
    topicName: String,
    workerId: String,
    errorMessage: String,
    executionId: String,
    lockExpirationTime: String,
    activityId: String,
    activityInstanceId: String,
    processInstanceId: String,
    processDefinitionId: String,
    tenantId: String,
    retries: Integer,
    priority: Integer,
    variables: Map[String, VariableValue]) {

  def variable[T](name: String): Option[T] = {
    variables.get(name) match {
      case Some(variableValue)  => variableValue.asValue match {
        case None    => None
        case value   => Some(variableValue.asTypedValue[T])
      }
      case None                 => None
    }
  }

}
package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class Topic(
    topicName: String,
    lockDuration: Int,
    variables: List[String] = List()
)

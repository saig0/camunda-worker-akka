package org.camunda.worker

/**
 * @author Philipp Ossler
 */

case class PollRequest(
    val consumerId: String, 
    val topicName: String, 
    val maxTasks: Int, 
    val lockTimeInSeconds: Int = 60, 
    val variableNames: List[String] = List())
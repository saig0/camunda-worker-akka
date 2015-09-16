package org.camunda.worker.akka.client

import dispatch._
import dispatch.Defaults._
import net.liftweb.json._
import net.liftweb.json.Serialization._
import com.ning.http.client.RequestBuilder
import java.text.SimpleDateFormat

/**
 * Rest Client for camunda BPM Server.
 * 
 * @author Philipp Ossler
 */
class CamundaClient(hostAdress: String) {
  
  // using defaults for json serialization
  implicit val formats = new DefaultFormats {
      // change date format to parse response format
      override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    }
  
  val jsonHeader = Map("Content-Type" -> "application/json")
  
  val externalTaskUri = url(hostAdress) / "external-task"
  
  def pollTasks(pollRequest: PollAndLockTaskRequest): Future[LockedTasksResponse] = {
    val json: String = write(pollRequest)    
    val request = jsonRequest(externalTaskUri / "poll" POST, json)
    
    handleRequest(request, json => 
      parse(json).extract[LockedTasksResponse])
  }
  
  def taskCompleted(taskId: String, completedRequest: CompleteTaskRequest): Future[Unit] =  {
    val json: String = write(completedRequest)
    val request = jsonRequest(externalTaskUri / taskId / "complete" POST, json)
    
    handleRequest(request, _ => ())
  }
  
  def taskFailed(taskId: String, failedRequest: FailedTaskRequest): Future[Unit] = {
    val json: String = write(failedRequest)
    val request = jsonRequest(externalTaskUri / taskId / "failed" POST, json)
    
    handleRequest(request, _ => ())
  }
  
  private def jsonRequest(request: Req, json: String): Req = {
    request << json <:< jsonHeader
  }

  private def handleRequest[T](request: Req, handler: String => T): Future[T] =
    for (result <- Http(request OK as.String).either) yield {
      result match {
        case Right(content) => handler(content)
        case Left(StatusCode(404)) => throw new RuntimeException("request failed: server is not available (404)")
        case Left(StatusCode(code)) => throw new RuntimeException("request failed: " + code)
        case Left(e) => throw new RuntimeException("request failed: " + e)
      }
    }
  
}
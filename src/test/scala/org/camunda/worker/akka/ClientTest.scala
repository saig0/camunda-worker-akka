package org.camunda.worker.akka

import net.liftweb.json._
import net.liftweb.json.Serialization._
import org.scalatest.FlatSpec
import org.camunda.worker.akka.client.LockedTasksResponse
import org.scalatest.Matchers
import org.joda.time.DateTime
import java.text.SimpleDateFormat
import net.liftweb.json.ext.DateParser
import org.camunda.worker.akka.client.LockedTasksResponse
import org.camunda.worker.akka.client.LockedTask
import org.camunda.worker.akka.client.LockedTasksResponse
import org.camunda.worker.akka.client.LockedTasksResponse
import org.camunda.worker.akka.client.LockedTask

/**
 * @author Philipp Ossler
 */

case object MyDateTimeSerializer extends CustomSerializer[DateTime](format => (
  {
    case JString(s) => println(s); new DateTime(s) // new DateTime(DateParser.parse(s, format))
    case JNull => null
  },
  {
    case d: DateTime => JString(format.dateFormat.format(d.toDate))
  }
))

class ClientTest extends FlatSpec with Matchers {
  
  
  
  "The client" should "serialize date time" in {
    
    val json = """{
          "id":"03098548-5c51-11e5-9309-5cc5d4130e48",
          "topicName":"reserveOrderItems",
          "lockTime":"2015-09-16T10:58:58",
          "activityId":"sid-F2EC54E7-D69F-466B-96A3-78385F1DB3CB",
          "activityInstanceId":"03098547-5c51-11e5-9309-5cc5d4130e48",
          "processInstanceId":"03095e34-5c51-11e5-9309-5cc5d4130e48", 
          "processDefinitionId":"4d7c0850-4d4e-11e5-8739-5cc5d4130e48",
          "variables":{
            "var1":{
              "type":"Null",
              "value":null,
              "valueInfo":{
              }
            }
           }
          }"""
 
    implicit val formats = new DefaultFormats {
      override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    }
    
    val response: LockedTask = parse(json).extract[LockedTask]
    
    response.topicName should equal("reserveOrderItems")
  }
  
  it should "serialize response" in {
    val json = """{"tasks":[{"id":"03098548-5c51-11e5-9309-5cc5d4130e48","topicName":"reserveOrderItems","lockTime":"2015-09-16T10:58:58","activityId":"sid-F2EC54E7-D69F-466B-96A3-78385F1DB3CB","activityInstanceId":"03098547-5c51-11e5-9309-5cc5d4130e48","processInstanceId":"03095e34-5c51-11e5-9309-5cc5d4130e48","processDefinitionId":"4d7c0850-4d4e-11e5-8739-5cc5d4130e48","variables":{"var1":{"type":"Null","value":null,"valueInfo":{}}}}]}"""
 
    implicit val formats = new DefaultFormats {
      override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    }
      
    val response: LockedTasksResponse = parse(json).extract[LockedTasksResponse] 
    
    response.tasks should have length 1 
  }
  
}
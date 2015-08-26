

package org.camunda.worker/**
 * @author Philipp Ossler
 */

import scala.collection.JavaConversions._
import org.camunda.worker.dto.LockedTaskDto

object Main extends App {
 
  println("started...........")
  
  // TODO poll in loop
  val tasks = new Poller("http://localhost:8080/engine-rest").poll("reserveOrderItems", "akka")
  
  println(s"receive $tasks")
  
  // TODO execute the tasks
  tasks.getTasks.foreach( executeTask )
  
  
  def executeTask(task: LockedTaskDto) {
    println(s"execute $task")
  }
}


package org.camunda.worker/**
 * @author Philipp Ossler
 */

object Main extends App {
 
  println("started...........")
  
  // TODO poll in loop
  val tasks = new Poller("http://localhost:8080/engine-rest").poll("reserveOrderItems", "akka")
  
  // TODO execute the tasks
  
}
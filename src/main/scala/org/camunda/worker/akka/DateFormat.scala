package org.camunda.worker.akka
import java.text.SimpleDateFormat
import java.util.Date


object DateFormat {
  
  val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

  def parse(date: String): Date = format.parse(date)
  
  def format(date: Date) : String = format.format(date)
  
}
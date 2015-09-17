package org.camunda.worker.akka.client

import org.camunda.worker.akka.DateFormat
import java.util.Date
import scala.util.parsing.json.JSON
import net.liftweb.json.JsonParser
import scala.xml.XML
import scala.xml.Elem
import net.liftweb.json.JsonAST.JValue

/**
 * @author Philipp Ossler
 */
case class VariableValue(
    `type`: String,
    value: String,
    valueInfo: Map[String, Any] = Map()) {

  def asValue: Any =
    `type` match {
      case "Null"     => None
      case "String"   => value
      case "Boolean"  => value.toBoolean
      case "Short"    => value.toShort
      case "Integer"  => value.toInt
      case "Long"     => value.toLong
      case "Double"   => value.toDouble
      case "Date"     => DateFormat.parse(value)
      case "Xml"      => XML.loadString(value)
      case "Json"     => JsonParser.parse(value)
      case _          => throw new IllegalArgumentException(s"unable to cast value of type '${`type`}' into scala object")
    }

  def asTypedValue[T]: T = {
    val scalaValue = asValue
    // may throw an class cast exception
    scalaValue.asInstanceOf[T]
  }
}

object VariableValue {

  implicit def noneToVariableValue(value: Nothing): VariableValue = VariableValue("Null", "null")
  implicit def stringToVariableValue(value: String): VariableValue = VariableValue("String", value)
  implicit def booleanToVariableValue(value: Boolean): VariableValue = VariableValue("Boolean", value.toString)
  implicit def shortToVariableValue(value: Short): VariableValue = VariableValue("Short", value.toString)
  implicit def integerToVariableValue(value: Integer): VariableValue = VariableValue("Integer", value.toString)
  implicit def longToVariableValue(value: Long): VariableValue = VariableValue("Long", value.toString)
  implicit def doubleToVariableValue(value: Double): VariableValue = VariableValue("Double", value.toString)
  implicit def dateToVariableValue(value: Date): VariableValue = VariableValue("Date", DateFormat.format(value))
  implicit def xmlToVariableValue(value: Elem): VariableValue = VariableValue("Xml", value.toString)
  implicit def jsonToVariableValue(value: JValue): VariableValue = VariableValue("Json", value.toString)

}
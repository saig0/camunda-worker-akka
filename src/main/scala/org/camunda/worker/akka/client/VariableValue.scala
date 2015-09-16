package org.camunda.worker.akka.client

/**
 * @author Philipp Ossler
 */
case class VariableValue(
  `type`: String,
  value: String,
  valueInfo: Map[String, Any] = Map()
) {
  
  def toValue[T]: T = {
    val typedValue = `type` match {
      case "Null"    => None
      case "String"  => value
      case "Integer" => value.toInt
      case "Long"    => value.toLong
      case "Float"   => value.toFloat
      case "Double"  => value.toDouble
      case _ => throw new IllegalArgumentException(s"unable to cast value of type '${`type`}' into scala object")
    }
    // may throw an class cast exception
    typedValue.asInstanceOf[T]
  }
}

object VariableValue {
  
  implicit def anyToVariableValue(obj: Any): VariableValue = fromValue(obj)
  
  def fromValue(value: Any): VariableValue = {
    value match {
      case None      => VariableValue("Null", "null")
      case s: String => VariableValue("String", s)
      case i: Int    => VariableValue("Integer", i.toString)
      case l: Long   => VariableValue("Long", l.toString)
      case f: Float  => VariableValue("Float", f.toString)
      case d: Double => VariableValue("Double", d.toString)
      case _         => throw new IllegalArgumentException(s"unable to transform value '$value'' into VariableValue object")
    }
  }
}
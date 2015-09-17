
# camunda-worker-akka

Implement workers for external tasks in [Camunda BPM](http://camunda.org) in Scala, based on [Akka](http://akka.io).

> Alternative Versions: 
* [Java](https://github.com/nikku/camunda-worker-java)
* [NodeJS](https://github.com/nikku/camunda-worker-node)

## Summary

This tool provides a Scala template to external tasks exposed by the process engine.
You can build a scala worker based on the template.

## Getting started

> Requirements
* [SBT](http://www.scala-sbt.org) to build the application 

Test the application local with
```
sbt "test:runMain org.camunda.worker.akka.Main"
```

Build the application with
```
sbt assemply
```

Deploy the application with
```
sbt pulishLocal
```

See example of using the akka worker in [Order Processing Microservices example](https://github.com/meyerdan/order-processing-microservices/tree/master/payment).

## Using the template

Write a launch class:
```scala
object Main extends App {
 
  // create actor system
  val system = ActorSystem("MyActorSystem")
  
  // create worker
  val worker = system.actorOf(Props[PaymentWorker], name = "payment-worker")
  
  // start polling
  val pollActor = system.actorOf(PollActor.props(hostAddress = "http://localhost:8080/engine-rest", maxTasks = 5, waitTime= 100, lockTime = 600))
  pollActor ! Poll(topicName = "payment", worker, variableNames = List("orderId"))
  
}
```

Write a worker:
```scala
class PaymentWorker extends Worker {

  def work(task: LockedTask): Map[String, VariableValue] = {
    // resolve variables from process instance
    val orderId = task.variables.get("orderId") match {
      case Some(variableValue)  => variableValue.asValue[String]
      case None                 => throw IllegalArgumentException("no order id available")
    }
  
    // do the work
    val payment = calculatePayment(orderId)
    // return the result which will set as variable of the process instance
    Map("payment" -> payment)
  }
  
}
```

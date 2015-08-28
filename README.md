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
  val worker = system.actorOf(Props[PaymentWorker], name = "worker-1")
  
  // start polling
  val pollActor = system.actorOf(PollActor.props(hostAddress = "http://localhost:8080/engine-rest", maxTasks = 5, waitTime= 100, lockTime = 600), name = "poller")
  pollActor ! Poll(topicName = "payment", worker)
  
}
```

Write a worker:
```scala
class PaymentWorker extends Worker {
  
  def work(task: LockedTaskDto) {
    // working...
  }
}
```

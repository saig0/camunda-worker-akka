# camunda-worker-akka

Implement workers for external tasks in [Camunda BPM](http://camunda.org) in Scala, based on [Akka](http://akka.io).

> Alternative Versions: 
* [Java](https://github.com/nikku/camunda-worker-java)
* [NodeJS](https://github.com/nikku/camunda-worker-node)

## Summary

This tool provides a Scala interface to external tasks exposed by the process engine.

## Getting started

> Requirements
* [SBT](http://www.scala-sbt.org) to run and build the application 

Run application with
```
sbt run
```

(local test with : sbt "test:runMain org.camunda.worker.akka.Main")

Build application with
```
sbt assemply
```

The running application can shut down by press ENTER.


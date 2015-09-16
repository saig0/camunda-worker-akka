organization := "org.camunda"

name := "camunda-worker-akka"

version := "1.0.0-SNAPSHOT"

scalaVersion := "2.11.7"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.12",
  "com.typesafe.akka" %% "akka-remote" % "2.3.12",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.12"  
 )

libraryDependencies ++= Seq(
  "net.liftweb" %% "lift-json" % "2.6.2",
  "net.liftweb" %% "lift-json-ext" % "2.6.2",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.3",
  "com.github.nscala-time" %% "nscala-time" % "2.2.0"
)

libraryDependencies += "org.springframework" % "spring-context" % "4.2.0.RELEASE"
libraryDependencies += "org.springframework" % "spring-web" % "4.2.0.RELEASE"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.6.1"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.6.1"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.6.1"
 
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.13"
 
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
libraryDependencies += "junit" % "junit" % "4.11" % "test"


assemblyMergeStrategy  in assembly := {
  case x if x.startsWith("META-INF") => MergeStrategy.discard // Bumf
  case x if x.endsWith(".html") => MergeStrategy.discard // More bumf
  case x if x.contains("slf4j-api") => MergeStrategy.last
  case x if x.contains("org/cyberneko/html") => MergeStrategy.first
  case PathList("com", "esotericsoftware", xs@_ *) => MergeStrategy.last // For Log$Logger.class
  case x =>
     val oldStrategy = (mergeStrategy in assembly).value
     oldStrategy(x)
}
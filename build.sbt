name := "moviestore_akka"

version := "1.0"

scalaVersion := "2.9.1"

parallelExecution in Test := false

libraryDependencies +=
       "org.scala-tools.testing" %% "scalacheck" % "1.9" % "test"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "se.scalablesolutions.akka" % "akka-actor" % "1.3"

libraryDependencies += "se.scalablesolutions.akka" % "akka-remote" % "1.3"

libraryDependencies += "se.scalablesolutions.akka" % "akka-stm" % "1.3"

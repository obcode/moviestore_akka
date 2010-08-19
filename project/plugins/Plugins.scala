import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  val akkaPlugin = "se.scalablesolutions.akka" % "akka-sbt-plugin" % "0.10"
}

// vim: set ts=2 sw=4 et:

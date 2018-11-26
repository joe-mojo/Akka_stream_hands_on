import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4"
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.5.15"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.1.5"
  lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.2"
  lazy val logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  lazy val scalaLogging = "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

}

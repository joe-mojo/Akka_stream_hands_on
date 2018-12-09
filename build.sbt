import Dependencies._
import sbt.Keys._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "org.jro",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "Akka_Stream_hands_on",
    test in assembly := {},
    mainClass in Compile := Some("org.jro.exercise.akkastream.hchallenge.App"),
    assemblyJarName in assembly := s"akka-stream-hands-on-hchallenge-${version.value}.jar",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      akkaStream,
      akkaHttp,
      typesafeConfig,
      scalaLogging,
      logback
    )
  )

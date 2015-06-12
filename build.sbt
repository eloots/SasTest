organization := "com.sequoia"

name := "sasjavaclient"

version := "1.0"
 
scalaVersion := Version.scala

libraryDependencies ++= Dependencies.SAS_Testing

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

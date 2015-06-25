organization := "com.sequoia"

name := "sasUnitTesting"

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

fork := true


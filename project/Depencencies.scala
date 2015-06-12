/**
 * Copyright 2015 Eric Loots
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import sbt._

object Version {
  val akka         = "2.3.6"
  val logback      = "1.1.2"
  val scala        = "2.11.4"
  val scalaParsers = "1.0.2"
  val scalaTest    = "2.2.4"
  val commonsio    = "1.3.2"
  val scalactic    = "2.2.4"
}

object Library {
  val akkaActor      = "com.typesafe.akka"      %% "akka-actor"               % Version.akka
  val akkaSlf4j      = "com.typesafe.akka"      %% "akka-slf4j"               % Version.akka
  val akkaTestkit    = "com.typesafe.akka"      %% "akka-testkit"             % Version.akka
  val logbackClassic = "ch.qos.logback"         %  "logback-classic"          % Version.logback
  val scalaParsers   = "org.scala-lang.modules" %% "scala-parser-combinators" % Version.scalaParsers
  val scalaTest      = "org.scalatest"          %% "scalatest"                % Version.scalaTest
  val commonsio      = "org.apache.commons"     % "commons-io"                % Version.commonsio
  val scalactic      = "org.scalactic"          % "scalactic_2.11"            % Version.scalactic
}

object Dependencies {

  import Library._

  val SAS_Testing = List(
    commonsio,
    scalaParsers,
    scalactic,
    logbackClassic,
    scalaTest % "test"
  )
}

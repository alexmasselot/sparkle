/* Copyright 2013  Nest Labs

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.  */

import sbt._

object Dependencies {
  object V {
    val scalaTest = "2.0"
    val akka = "2.2.3"
    val spray = "1.2.0"
    val rxJava = "0.15.1"
  }

  // Spray + Akka
  val sprayCan              = "io.spray"                  %  "spray-can"              % V.spray  
  val sprayRouting          = "io.spray"                  %  "spray-routing"          % V.spray  
  val sprayClient           = "io.spray"                  %  "spray-client"           % V.spray  
  val sprayCaching          = "io.spray"                  %  "spray-caching"          % V.spray  
  val sprayJson             = "io.spray"                  %% "spray-json"             % "1.2.5"
  val akkaActor             = "com.typesafe.akka"         %% "akka-actor"             % V.akka
  val akkaSlf4j             = "com.typesafe.akka"         %% "akka-slf4j"             % V.akka
  val akkaRemoting          = "com.typesafe.akka"         %% "akka-remote"            % V.akka

  val argot                 = "org.clapper"               %% "argot"                  % "1.0.0"
  val nScalaTime            = "com.github.nscala-time"    %% "nscala-time"            % "0.4.2"
  val openCsv               = "net.sf.opencsv"            %  "opencsv"                % "2.3"
  val cassandra             = "org.apache.cassandra"      % "cassandra-all"           % "2.0.3"
  val cassandraClient       = "com.datastax.cassandra"    % "cassandra-driver-core"   % "2.0.0-rc1"

  val scalaReflect          = "org.scala-lang"            % "scala-reflect"           % "2.10.3"

  val rxJavaCore            = "com.netflix.rxjava"        % "rxjava-core"             % "0.15.1"
  val rxJavaScala           = "com.netflix.rxjava"        % "rxjava-scala"            % "0.15.1"  intransitive()
              

  object Runtime {
    val logback              = "ch.qos.logback"            % "logback-classic"         % "1.0.9"     
  }

  object Test {
    val scalaTest            = "org.scalatest"            %% "scalatest"              % V.scalaTest % "test"
    val scalaCheck           = "org.scalacheck"           %% "scalacheck"             % "1.10.0"    % "test"
    val sprayTestKit         = "io.spray"                 %  "spray-testkit"          % V.spray     % "test"
    val akkaTestKit          = "com.typesafe.akka"        %% "akka-testkit"           % V.akka      % "test"
  }

  object IT {
    val scalaTest            = "org.scalatest"            %% "scalatest"              % V.scalaTest % "it"
    val scalaCheck           = "org.scalacheck"           %% "scalacheck"             % "1.10.0"    % "it"
    val sprayTestKit         = "io.spray"                 %  "spray-testkit"          % V.spray     % "it"
    val akkaTestKit          = "com.typesafe.akka"        %% "akka-testkit"           % V.akka      % "it"
  }

}

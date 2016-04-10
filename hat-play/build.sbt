name := """hat-play"""

version := "0.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"

import com.typesafe.sbt.packager.docker._
dockerCommands ++= Seq(Cmd("WORKDIR", "/tmp"),Cmd("WORKDIR", "/opt/docker"))

libraryDependencies += "org.scalatest"  %% "scalatest"   % "2.2.4" % Test
libraryDependencies += "com.typesafe.akka" % "akka-testkit_2.11" % "2.4.1"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.1"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.4.1"
libraryDependencies += "com.github.wookietreiber" %% "scala-chart" % "latest.integration"
libraryDependencies += "com.itextpdf" % "itextpdf" % "5.5.6"
libraryDependencies += "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % "2.4.2"
libraryDependencies += "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.4.2"
libraryDependencies += "io.spray" %%  "spray-json" % "1.3.2"
libraryDependencies += "com.typesafe.akka" % "akka-stream-experimental_2.11" % "2.0.3"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

fork in run := true

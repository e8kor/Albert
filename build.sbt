import sbt._
import sbtbuildinfo.Plugin._
import Resolver.{sonatypeRepo, typesafeRepo}

import scala.language.postfixOps

lazy val thirdParties = Seq(
  "org.scalaz"  %% "scalaz-core" % "7.0.6" withSources(),
  "com.chuusai" %% "shapeless"   % "2.2.0-RC4" withSources(),
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.5" withSources(),
  "org.apache.activemq" %  "activemq-camel" % "5.11.1" withSources(),
  "net.ceedubs"         %% "ficus"          % "1.1.2"  withSources()
)

lazy val scalaLibraries = Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.6" withSources()
)

lazy val akka = Seq(
  "com.typesafe.akka" %% "akka-actor"   % "2.3.9" withSources(),
  "com.typesafe.akka" %% "akka-contrib" % "2.3.9" withSources(),
  "com.typesafe.akka" %% "akka-remote"  % "2.3.9" withSources(),
  "com.typesafe.akka" %% "akka-camel"   % "2.3.9" withSources(),
  "com.typesafe.akka" %% "akka-kernel"  % "2.3.9" withSources()
) ++ Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.3.9" % "test" withSources()
)

lazy val typesafe = Seq(
  "com.typesafe"                %  "config"        % "1.2.0" withSources(),
  "com.typesafe.scala-logging"  %% "scala-logging" % "3.1.0" withSources()
)

lazy val unitTest = Seq(
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2"   % "test" withSources(),
  "org.scalatest" %  "scalatest_2.11"              % "2.2.4" % "test" withSources()
)

val commonSettings = buildInfoSettings ++ Seq (
  scalaVersion in ThisBuild := "2.11.6",
  resolvers ++= (Seq("snapshots", "releases") map sonatypeRepo) ++ (Seq("snapshots", "releases", "repo") map typesafeRepo),
  libraryDependencies ++= thirdParties ++ akka ++ typesafe ++ unitTest,
  buildInfoPackage := "org.system.info",
  buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion),
  sourceGenerators in Compile <+= buildInfo
)

val macroSettings = Seq(
  libraryDependencies ++= scalaLibraries,
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross (CrossVersion full))
)

val rootSettings = Seq(
  run <<= run in Compile in test
)

lazy val root = project in file(".") settings ( commonSettings ++ rootSettings:_* ) aggregate(macros, core, test, api, rtp)

lazy val rtp = project in file("rtp-plugin") settings (  commonSettings :_*) dependsOn api

lazy val macros = project in file("macros") settings ( macroSettings:_*)

lazy val core = project in file("core") settings ( commonSettings :_*) dependsOn api

lazy val api = project in file("api") settings ( macroSettings ++ commonSettings :_*)

lazy val test = project in file("test") settings ( macroSettings ++ commonSettings:_*) dependsOn macros


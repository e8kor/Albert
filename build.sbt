import sbt._
import sbtbuildinfo.Plugin._
import Resolver.{sonatypeRepo, typesafeRepo}

import scala.language.postfixOps

lazy val thirdParties = Seq(
  "org.scalaz"  %% "scalaz-core" % "7.0.6" withSources(),
  "com.chuusai" %% "shapeless"   % "2.0.0" withSources(),
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.5" withSources(),
  "org.apache.activemq" % "activemq-camel" % "5.11.1" withSources()
)

lazy val scalaLibraries = Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.6" withSources()
)

lazy val akka = Seq(
  "com.typesafe.akka" %% "akka-actor"   % "2.3.6" withSources(),
  "com.typesafe.akka" %% "akka-contrib" % "2.3.6" withSources(),
  "com.typesafe.akka" %% "akka-remote"  % "2.3.6" withSources(),
  "com.typesafe.akka" %% "akka-camel"   % "2.3.6" withSources(),
  "com.typesafe.akka" %% "akka-kernel"  % "2.3.6" withSources()
) ++ Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.3.6" % "test" withSources()
)

lazy val typesafe = Seq(
  "com.typesafe"                %  "config"        % "1.2.0" withSources(),
  "com.typesafe.scala-logging"  %% "scala-logging" % "3.1.0" withSources()
)

lazy val macwaremill = Seq(
  "com.softwaremill.macwire" %% "macros"  % "0.7" withSources(),
  "com.softwaremill.macwire" %% "runtime" % "0.7" withSources()
)

lazy val unitTest = Seq(
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2"   % "test" withSources(),
  "org.scalatest" %  "scalatest_2.11"              % "2.2.4" % "test" withSources()
)

val commonSettings = buildInfoSettings ++ Seq (
  scalaVersion in ThisBuild := "2.11.6",
  resolvers ++= (Seq("snapshots", "releases") map sonatypeRepo) ++ (Seq("snapshots", "releases", "repo") map typesafeRepo),
  libraryDependencies ++= thirdParties ++ scalaLibraries ++ akka ++ typesafe ++ macwaremill ++ unitTest,
  buildInfoPackage := "org.system.info",
  buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion),
  sourceGenerators in Compile <+= buildInfo
)

val compilerPluginsSettings = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full)
)

val allSettings = commonSettings ++ compilerPluginsSettings

val rootCompilationSettings = Seq(
  run <<= run in Compile in core,
  run <<= run in Compile in test
)

lazy val root = project in file(".") settings (commonSettings ++ rootCompilationSettings:_*) aggregate(macros, core, test)

lazy val macros = project in file("macros") settings ( allSettings:_*)

lazy val core = project in file("core") settings ( commonSettings :_*)

lazy val test = project in file("test") settings ( allSettings:_*) dependsOn macros


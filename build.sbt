import sbt._
import Keys._
import sbtassembly.Plugin._
import sbtbuildinfo.Plugin._
import AssemblyKeys._

name := "albert"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.0"

autoCompilerPlugins := true

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo) ++ Seq("snapshots", "releases", "repo").map(Resolver.typesafeRepo)

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.0" withSources()
) ++ Seq(
  "com.typesafe.akka" %% "akka-actor"   % "2.3.6" withSources(),
  "com.typesafe.akka" %% "akka-contrib" % "2.3.6" withSources(),
  "com.typesafe.akka" %% "akka-remote"  % "2.3.6" withSources(),
  "com.typesafe.akka" %% "akka-camel"   % "2.3.6" withSources(),
  "com.typesafe.akka" %% "akka-kernel"  % "2.3.6" withSources()
) ++ Seq(
  "org.scalaz"  %% "scalaz-core" % "7.0.6" withSources(),
  "com.chuusai" %% "shapeless"   % "2.0.0" withSources()
) ++ Seq(
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.5" withSources(),
  "com.typesafe"                %  "config"     % "1.2.0" withSources()
) ++ Seq(
  "com.typesafe.scala-logging"  %% "scala-logging" % "3.1.0" withSources()
) ++ Seq(
  "com.typesafe.akka" %% "akka-testkit"                % "2.3.6" % "test" withSources(),
  "org.scalamock"     %% "scalamock-scalatest-support" % "3.1.1" % "test" withSources(),
  "junit"             %  "junit"                       % "4.11"  % "test" withSources(),
  "org.scalatest"     %  "scalatest_2.11"              % "2.2.0" % "test" withSources()
)++ Seq(
  "com.softwaremill.macwire" %% "macros" % "0.7" withSources(),
  "com.softwaremill.macwire" %% "runtime" % "0.7" withSources()
)

buildInfoSettings

buildInfoPackage := "org.system.info"

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

sourceGenerators in Compile <+= buildInfo

assemblySettings

jarName in assembly := "system.jar"

mainClass in assembly := Some("org.system.dev.main.Main")

addCompilerPlugin("org.brianmckenna" %% "wartremover" % "0.10")

scalacOptions in (Compile, compile) += "-P:wartremover:only-warn-traverser:org.brianmckenna.wartremover.warts.Unsafe"
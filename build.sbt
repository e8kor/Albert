import sbt._
import Keys._
import sbtassembly.Plugin._
import sbtbuildinfo.Plugin._
import AssemblyKeys._

name := "core"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.1"

autoCompilerPlugins := true

resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo) ++ Seq("snapshots", "releases", "repo").map(Resolver.typesafeRepo)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % "2.3.3",
  "com.typesafe.akka" %% "akka-contrib" % "2.3.3"
) ++ Seq(
  "org.scalaz"  %% "scalaz-core" % "7.0.6",
  "com.chuusai" %% "shapeless"   % "2.0.0"
) ++ Seq(
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.5",
  "com.typesafe"                %  "config"     % "1.2.0"
) ++ Seq(
  "org.slf4j"      % "slf4j-api"       % "1.7.2",
  "ch.qos.logback" % "logback-classic" % "1.1.1",
  "ch.qos.logback" % "logback-core"    % "1.1.1"
) ++ Seq(
  "com.typesafe.akka" %% "akka-testkit"                % "2.3.3" % "test",
  "org.scalamock"     %% "scalamock-scalatest-support" % "3.1.1" % "test",
  "junit"             % "junit"                        % "4.11"  % "test",
  "org.scalatest"     %  "scalatest_2.11"              % "2.2.0" % "test"
)++ Seq(
  "com.softwaremill.macwire" %% "macros" % "0.7",
  "com.softwaremill.macwire" %% "runtime" % "0.7"
)

buildInfoSettings

buildInfoPackage := "com.rtp.info"

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

sourceGenerators in Compile <+= buildInfo

assemblySettings

jarName in assembly := "rtp-core.jar"

mainClass in assembly := Some("com.rtp.dev.main.Main")

addCompilerPlugin("org.brianmckenna" %% "wartremover" % "0.10")

scalacOptions in (Compile, compile) += "-P:wartremover:only-warn-traverser:org.brianmckenna.wartremover.warts.Unsafe"

//scalacOptions in (Compile, compile) += "-P:wartremover:traverser:org.brianmckenna.wartremover.warts.Unsafe"

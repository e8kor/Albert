import sbt.Resolver.{sonatypeRepo, typesafeRepo}
import sbt._
import sbtbuildinfo.Plugin._

import scala.language.postfixOps

lazy val rtpLibraries = Seq(
  "org.apache.velocity" % "velocity" % "1.7" withSources(),
  "org.apache.poi" % "poi" % "3.12-beta1" withSources(),
  "org.apache.poi" % "poi-ooxml" % "3.12-beta1" withSources(),
  "info.folone" %% "poi-scala" % "0.14" withSources()
)

lazy val mainLibraries = Seq(
  "com.typesafe" % "config" % "1.2.0" withSources(),
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0" withSources()
) ++ Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.11" withSources(),
  "com.typesafe.akka" %% "akka-contrib" % "2.3.11" withSources(),
  "com.typesafe.akka" %% "akka-remote" % "2.3.11" withSources(),
  "com.typesafe.akka" %% "akka-camel" % "2.3.11" withSources(),
  "com.typesafe.akka" %% "akka-kernel" % "2.3.11" withSources()
) ++ Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11" % "test" withSources()
) ++ Seq(
  "org.scalamock" %% "scalamock-scalatest-support" % "3.2" % "test" withSources(),
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test" withSources()
) ++ Seq(
  "org.scalaz" %% "scalaz-core" % "7.1.1" withSources(),
  "org.scalaz.stream" %% "scalaz-stream" % "0.7a" withSources(),
  "com.chuusai" %% "shapeless" % "2.2.0-RC4" withSources(),
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.5" withSources(),
  "org.apache.activemq" % "activemq-camel" % "5.11.1" withSources(),
  "net.ceedubs" %% "ficus" % "1.1.2" withSources()
) ++ Seq(
  "org.scala-lang" % "scala-reflect" % "2.11.6" withSources()
)

val commonSettings = buildInfoSettings ++ Seq(
  scalaVersion in ThisBuild := "2.11.6",
  resolvers ++= (Seq("snapshots", "releases") map sonatypeRepo) ++ (Seq("snapshots", "releases", "repo") map typesafeRepo) :+ ("Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"),
  libraryDependencies ++= mainLibraries,
  buildInfoPackage := "org.system.info",
  buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion),
  sourceGenerators in Compile <+= buildInfo
)

val sparkSettings = commonSettings ++ Seq(
  libraryDependencies ++= mainLibraries,
  buildInfoPackage := "org.system.spark.info"
)

val rtpSettings = commonSettings ++ Seq(
  libraryDependencies ++= rtpLibraries
)

val macroSettings = commonSettings ++ Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross (CrossVersion full))
)

val rootSettings = Seq(
  run <<= run in Compile in test
)

lazy val root = project in file(".") settings (rootSettings: _*) aggregate(macros, core, test, api, rtp)

lazy val rtp = project in file("rtp-plugin") settings (rtpSettings: _*) dependsOn api

lazy val macros = project in file("macros") settings (macroSettings: _*)

lazy val core = project in file("core") settings (commonSettings: _*) dependsOn api

lazy val api = project in file("api") settings (macroSettings: _*)

lazy val spark = project in file("spark") settings (sparkSettings: _*)

lazy val test = project in file("test") settings (macroSettings: _*) dependsOn macros

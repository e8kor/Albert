name := "AlbertCorePlugins"

normalizedName := "albert_core_plugins"

description := """Automated system for structural suite execution.
                 |Core plugins its minimal set of plugins with which system can work, also its a source for creation new plugins
                 |More things comming...""".stripMargin

startYear := Some(2014)

developers := List(
  Developer("e8kor", "IEvgenii Korniichuk", "nutscracker.ua@gmail.com", url("https://github.com/e8kor"))
)

scmInfo := Some(
  ScmInfo(
    url("https://github/e8kor/AlbertCorePlugins.git"),
    "git@github.com/e8kor/AlbertCorePlugins.git"
  )
)

licenses in Global +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/e8kor/AlbertCorePlugins"))

publishMavenStyle := true

organization := "e8kor"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6", "2.10.5")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4-M2" withSources(),
  "com.typesafe.akka" %% "akka-testkit" % "2.4-M2" % "test" withSources(),
  "org.scalatest" %% "scalatest" % "2.2.5" % "test" withSources(),
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided" withSources(),
  "com.typesafe" % "config" % "1.2.0" withSources(),
  "com.googlecode.scalascriptengine" %% "scalascriptengine" % "1.3.10" withSources(),
  "org.scala-lang" % "scala-compiler" % scalaVersion.value withSources()
)

buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion)

buildInfoPackage := s"${organization value}.${name value}.info"

enablePlugins(BuildInfoPlugin)

scalacOptions := Seq(
  "-deprecation",
  "-feature",
  "-encoding", "utf8",
  "-language:postfixOps",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Xcheckinit",
  "-Xfuture",
  "-Xlint",
  "-Xfatal-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard"
)
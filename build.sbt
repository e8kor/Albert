name := "AlbertEngine"

normalizedName := "albert_engine"

description :=
  """Automated system for structural suite execution.
    |Engine is responsible for instantiation of suites, content loading and execution.""".stripMargin

startYear := Some(2014)

developers := List(
  Developer("e8kor", "IEvgenii Korniichuk", "nutscracker.ua@gmail.com", url("https://github.com/e8kor"))
)

scmInfo := Some(
  ScmInfo(
    url("https://github/e8kor/Albert.git"),
    "git@github.com/e8kor/Albert.git"
  )
)

licenses in Global +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/e8kor/Albert"))

publishMavenStyle := true

organization := "e8kor"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6", "2.10.5")

// TODO good option to make system typed
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-typed-experimental" % "2.4-M2" withSources(),
  "com.typesafe.akka" %% "akka-actor" % "2.4-M2" withSources(),
  "com.typesafe.akka" %% "akka-contrib" % "2.4-M2" withSources(),
  "com.typesafe.akka" %% "akka-remote" % "2.4-M2" withSources(),
  "com.typesafe.akka" %% "akka-camel" % "2.4-M2" withSources(),
  "com.typesafe.akka" %% "akka-kernel" % "2.4-M2" withSources(),
  "com.typesafe.akka" %% "akka-testkit" % "2.4-M2" % "test" withSources(),
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.5" withSources(),
  "org.apache.activemq" % "activemq-camel" % "5.11.1" withSources(),
  "org.scalatest" %% "scalatest" % "2.2.5" % "test" withSources(),
  "com.typesafe" % "config" % "1.2.0" withSources(),
  "ch.qos.logback" % "logback-classic" % "1.1.3" % "runtime",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0" withSources()
)

packageSummary in Universal := "Albert Structural Execution System"

packageDescription in Universal := "Automated suite execution system"

maintainer in Universal := "IEvgenii Korniichuk <nutscracker.ua@gmail.com>"

wixProductId := "ce07be71-510d-414a-92d4-dff47631848a"

wixProductUpgradeId := "4552fb0e-e257-4dbd-9ecb-dba9dbacf424"

// TODO Need to understand whats wrong here
//scriptClasspath ++= Seq("../conf", "../scripts")

executableScriptName := "albert"

mainClass in Compile := Some("org.system.Main")

buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion)

buildInfoPackage := s"${organization value}.${name value}.info"

enablePlugins(JavaAppPackaging, BuildInfoPlugin)

//scalacOptions := Seq(
//  "-deprecation",
//  "-feature",
//  "-encoding", "utf8",
//  "-language:postfixOps",
//  "-language:higherKinds",
//  "-language:implicitConversions",
//  "-unchecked",
//  "-Xcheckinit",
//  "-Xfuture",
//  "-Xlint",
//  "-Xfatal-warnings",
//  "-Yno-adapted-args",
//  "-Ywarn-dead-code",
//  "-Ywarn-value-discard"
// )
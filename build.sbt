name := "AlbertPluginAPI"

normalizedName := "albert_plugin_api"

description := """Automated system for structural suite execution.
                 |Plugin api mainly designed to declare messages can be received by plugin to start execution
                 |and sent back to its suite with result bundle.
                 |More things comming...""".stripMargin

startYear := Some(2014)

developers := List(
  Developer("e8kor", "IEvgenii Korniichuk", "nutscracker.ua@gmail.com", url("https://github.com/e8kor"))
)

scmInfo := Some(
  ScmInfo(
    url("https://github/e8kor/AlbertPluginAPI.git"),
    "git@github.com/e8kor/AlbertPluginAPI.git"
  )
)

licenses in Global +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/e8kor/AlbertPluginAPI"))

publishMavenStyle := false

organization := "e8kor"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6", "2.10.5")

resolvers += (Resolver bintrayRepo("e8kor", "maven"))

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.0" withSources(),
  "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided" withSources(),
  "com.typesafe.akka" %% "akka-actor" % "2.4-M2" withSources()
)

buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion)

buildInfoPackage := s"${organization value}.${name value}.info"

bintrayOrganization in bintray := None

bintrayPackageLabels := Seq("albert", "plugin", "api")

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
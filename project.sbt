name := "Albert"

normalizedName := "albert"

description := "Automated system for command suite execution based on Akka actor system and plugins"

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

buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion)

buildInfoPackage := s"${organization value}.${name value}.info"

lazy val albert = project in file(".") dependsOn(engine, plugin_api, integration_api, plugins) aggregate(engine, plugin_api, integration_api, plugins)

lazy val plugins = project dependsOn plugin_api

lazy val plugin_api = project

lazy val engine = project dependsOn(plugin_api, integration_api)

lazy val integration_api = project
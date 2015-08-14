name := "AlbertService"

normalizedName := "albert_service"

description :=
  """Automated system for structural suite execution.
    |Service demonstrate ability of system to work as *nix like service.
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

publishMavenStyle := false

organization := "e8kor"

scalaVersion := "2.11.6"

version := "0.1-SNAPSHOT"

buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion)

buildInfoPackage := s"${organization value}.${name value}.info"

bintrayOrganization in bintray := None

bintrayPackageLabels := Seq("albert", "service", "server")

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
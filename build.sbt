name := "AlbertIntegrationAPI"

normalizedName := "albert_integration_api"

description := """Automated system for structural suite execution.
                 |Integration api mainly designed to declare messages that can be received via AlbertEngine.
                 |More things comming...""".stripMargin

startYear := Some(2014)

developers := List(
  Developer("e8kor", "IEvgenii Korniichuk", "nutscracker.ua@gmail.com", url("https://github.com/e8kor"))
)

scmInfo := Some(
  ScmInfo(
    url("https://github/e8kor/AlbertIntegrationAPI.git"),
    "git@github.com/e8kor/AlbertIntegrationAPI.git"
  )
)

licenses in Global +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/e8kor/AlbertIntegrationAPI"))

publishMavenStyle := false

organization := "e8kor"

scalaVersion := "2.11.6"

crossScalaVersions := Seq("2.11.6", "2.10.5")

resolvers += (Resolver bintrayRepo("e8kor", "maven"))

buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion)

buildInfoPackage := s"${organization value}.${name value}.info"

bintrayOrganization in bintray := None

bintrayPackageLabels := Seq("albert", "integration", "api")

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
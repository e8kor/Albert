organization in Global := "system.albert"

scalaVersion in Global := "2.11.6"

crossScalaVersions in Global := Seq("2.11.6", "2.10.5")

(common buildInfoPackagePath) := "org.system.info"

(common buildInfoPluginSettings)

scalacOptions in Global := Seq(
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
  "-Ywarn-value-discard")

licenses in Global +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

lazy val albert = project in file(".") enablePlugins BuildInfoPlugin aggregate( api, core, `rtp-plugin`, test, macros, spark)

lazy val api = project

lazy val core = project dependsOn api

lazy val `rtp-plugin` = project dependsOn api

lazy val docs = project dependsOn(core, api, `rtp-plugin`)

lazy val macros = project

lazy val test = project dependsOn macros

lazy val spark = project

(common promptSettings)
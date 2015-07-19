name := "albert"

organization in Global := "org.system"

scalaVersion in Global := "2.11.6"

crossScalaVersions in Global := Seq("2.11.6", "2.10.5")

//scalacOptions in Global := Seq(
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

licenses in Global +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

lazy val albert = project in file(".") enablePlugins BuildInfoPlugin aggregate( api, core )

lazy val api = project enablePlugins BuildInfoPlugin

lazy val core = project enablePlugins BuildInfoPlugin dependsOn (api,core_plugins)

lazy val core_plugins = project enablePlugins BuildInfoPlugin dependsOn api

//lazy val rtp_old = project

//lazy val rtp = project enablePlugins BuildInfoPlugin dependsOn api
//
//lazy val docs = project enablePlugins BuildInfoPlugin dependsOn(core, api, rtp)
//
//lazy val test = project enablePlugins BuildInfoPlugin

(common promptSettings)
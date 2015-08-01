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

//lazy val rtp_old = project

//lazy val rtp = project enablePlugins BuildInfoPlugin dependsOn api

//lazy val docs = project enablePlugins BuildInfoPlugin dependsOn(core, api, rtp)

//lazy val test = project enablePlugins BuildInfoPlugin


licenses in Global +=("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

homepage := Some(url("https://github.com/e8kor/Albert"))

publishMavenStyle := true

pomExtra in Global := {

  <scm>
    <connection>scm:git:github.com/e8kor/Albert.git</connection>
    <url>github.com/e8kor/Albert.git</url>
  </scm>

    <developers>
      <developer>
        <id>e8kor</id>
        <name>IEvgenii Korniichuk</name>
        <url>https://github.com/e8kor</url>
      </developer>
    </developers>

}

lazy val albert = project in file(".") enablePlugins (JavaAppPackaging, BuildInfoPlugin) aggregate( api, core )

lazy val api = project enablePlugins BuildInfoPlugin

lazy val core = project enablePlugins BuildInfoPlugin dependsOn (api,core_plugins)

lazy val core_plugins = project enablePlugins BuildInfoPlugin dependsOn api

(common nativePackagerSettings)

(common promptSettings)
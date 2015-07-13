import sbt.Keys._
import sbt.Resolver.{sonatypeRepo, typesafeRepo}
import sbt._
import sbtbuildinfo.BuildInfoKeys._

object common {

  val scalaTestVersion = SettingKey[String]("scalatest version")
  val scalaCheckVersion = SettingKey[String]("scalacheck version")
  val buildInfoPackagePath = SettingKey[String]("BuildInfo package")
  val akkaVersion = SettingKey[String]("Akka version")
  val scalaMockVersion = SettingKey[String]("Scala mock version")
  val camelVersion = SettingKey[String]("Camel version")
  val watcherVersion = SettingKey[String]("Watcher version")
  val ficusVersion = SettingKey[String]("Ficus config library version")
  val configVersion = SettingKey[String]("Typesafe Config version")
  val loggingVersion = SettingKey[String]("Typesafe logging version")
  val poiVersion = SettingKey[String]("Apache poi library version")
  val foloneVersion = SettingKey[String]("Scala poi wrapper library version")
  val velocityVersion = SettingKey[String]("Apache velocity library version")
  val scalazVersion = SettingKey[String]("Scalaz library version")
  val scalazStreamVersion = SettingKey[String]("Scalaz Stream library version")
  val shapelessVersion = SettingKey[String]("Shapeless library version")

  def testSettings = Seq(
    scalaTestVersion := "2.2.5",
    scalaCheckVersion := "1.12.3",
    akkaVersion := "2.3.11",
    scalaMockVersion := "3.2",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalaTestVersion.value % "test" withSources(),
      "org.scalacheck" %% "scalacheck" % scalaCheckVersion.value % "test" withSources(),
      "org.scalamock" %% "scalamock-scalatest-support" % scalaMockVersion.value % "test" withSources(),
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion.value % "test" withSources()
    )
  )

  def akkaLibraries = Seq(
    akkaVersion := "2.3.11",
    camelVersion := "5.11.1",
    watcherVersion := "0.1.5",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion.value withSources(),
      "com.typesafe.akka" %% "akka-contrib" % akkaVersion.value withSources(),
      "com.typesafe.akka" %% "akka-remote" % akkaVersion.value withSources(),
      "com.typesafe.akka" %% "akka-camel" % akkaVersion.value withSources(),
      "com.typesafe.akka" %% "akka-kernel" % akkaVersion.value withSources(),
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion.value % "test" withSources(),
      "com.beachape.filemanagement" %% "schwatcher" % watcherVersion.value withSources(),
      "org.apache.activemq" % "activemq-camel" % camelVersion.value withSources()
    )
  )

  def configLibraries = Seq(
    ficusVersion := "1.1.2",
    configVersion := "1.2.0",
//    loggingVersion := "3.1.0",
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % configVersion.value withSources(),
      "ch.qos.logback" % "logback-classic" % "1.1.3" % "runtime",
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
  //      "com.typesafe.scala-logging" %% "scala-logging" % loggingVersion.value withSources(),
      "net.ceedubs" %% "ficus" % ficusVersion.value withSources()
    )
  )

  def utilityLibraries = Seq(
  scalazStreamVersion := "0.7.1a",
  scalazVersion := "7.1.1",
  shapelessVersion := "2.2.0",
    resolvers ++= Seq("Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"),
    libraryDependencies ++= Seq(
      "org.scalaz" %% "scalaz-core" % scalazVersion.value withSources(),
      "org.scalaz.stream" %% "scalaz-stream" % scalazStreamVersion.value withSources(),
      "com.chuusai" %% "shapeless" % shapelessVersion.value withSources()
    )
  )

  def macrosSettings = Seq(
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0-M5" cross CrossVersion.full),

    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided"
    ) ++ (
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, scalaMajor)) if scalaMajor == 10 => Seq("org.scalamacros" %% "quasiquotes" % "2.1.0-M5")
        case _ => Nil
      }
      ),

    unmanagedSourceDirectories in Compile +=
      (sourceDirectory in Compile).value / "macros" / s"scala-${scalaBinaryVersion.value}"
  )

  def velocityLibraries = Seq(
    velocityVersion := "1.7",
    resolvers ++= Seq(sonatypeRepo("releases"), typesafeRepo("releases"), "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"),
    libraryDependencies ++= Seq(
      "org.apache.velocity" % "velocity" % velocityVersion.value withSources()
    )
  )

  def poiLibraries = Seq(
    poiVersion := "3.12",
    foloneVersion := "0.14",
    resolvers ++= Seq(sonatypeRepo("releases"), typesafeRepo("releases")),
    libraryDependencies ++= Seq(
      "org.apache.poi" % "poi" % poiVersion.value withSources(),
      "org.apache.poi" % "poi-ooxml" % poiVersion.value withSources(),
      "info.folone" %% "poi-scala" % foloneVersion.value withSources()
    )
  )

  def buildInfoPluginSettings = Seq(
    buildInfoPackagePath := s"${organization value}.${name value}.info",
    buildInfoKeys := Seq(name, version, scalaVersion, sbtVersion),
    buildInfoPackage := buildInfoPackagePath.value
  )

  def promptSettings = Prompt settings

  //  def publishSettings = Seq(
  //    publishMavenStyle := true,
  //    scmInfo := Some(ScmInfo(url("https://github.com/oncue/knobs"),
  //      "git@github.com:oncue/knobs.git")),
  //    bintrayPackageLabels := Seq("configuration", "functional programming", "scala", "reasonable"),
  //    bintrayOrganization := Some("oncue"),
  //    bintrayRepository := "releases",
  //    bintrayPackage := "knobs"
  //  )
}
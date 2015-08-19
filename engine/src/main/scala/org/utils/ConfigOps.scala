package org.utils

import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.{FiniteDuration, SECONDS, TimeUnit}
import scala.language.postfixOps
import scala.reflect.io.{Directory, Path}

class ConfigOps(val config: Config) extends AnyVal {

  def asRootConfig(configDir: Directory): RootConfig = {

    import scala.concurrent.duration.DurationInt
    RootConfig(
      rootDirectory = findDirectory("root_directory") getOrElse configDir,
      publishStatusIntialDelay = duration("publish_status_initial_delay") getOrElse (1 second),
      publishStatusInterval = duration("publish_status_interval_delay") getOrElse (5 seconds),
      autoStart = bool("auto_start")
    )
  }

  def findDirectory(path: String): Option[Directory] = {
    if (config hasPath path) {
      Some(Path(config getString path) toDirectory)
    } else {
      None
    }
  }

  def duration(path: String, timeUnit: TimeUnit = SECONDS): Option[FiniteDuration] = {
    import scala.concurrent.duration.pairLongToDuration
    if (config hasPath path) {
      Some((config getDuration(path, timeUnit)) -> timeUnit)
    } else {
      None
    }
  }

  def asSuiteConfig(suiteDirectory: Directory): SuiteConfig = {

    val pluginClasses = if (bool("is_runner_config"))
      getPluginsConfig("runners") map (new ConfigOps(_)) flatMap (pluginConf => pluginConf getClasses "main_class")
    else
      getClasses("runners")

    require(pluginClasses nonEmpty,
      s"""illegal config: suite runner not defined
          |passed dir: ${suiteDirectory path}
          |passed config: ${config toString}""" stripMargin)

    SuiteConfig(
      suiteDirectory = suiteDirectory,
      runnerParallelExecution = bool("runners_parallel_execution"),
      pluginClasses = pluginClasses
    )
  }

  def getClasses(path: String): Seq[Class[_]] = {
    import scala.collection.JavaConversions.asScalaBuffer
    if (config hasPath path) {
      config getStringList path map {
        clazz =>
          getClass() getClassLoader() loadClass clazz
      }
    } else {
      Seq()
    }
  }


  def getPluginsConfig(path: String): Seq[Config] = {
    import scala.collection.JavaConversions.asScalaBuffer
    if (config hasPath path) {
      (config getStringList path) map {
        fileName =>
          ConfigFactory load
      }
    } else {
      Seq()
    }
  }

  def bool(path: String) = {
    if (config hasPath path) {
      config getBoolean path
    } else {
      false
    }
  }

  def getClass(path: String): Class[_] = {
    findClass(path) getOrElse (sys error s"class not found by path - $path")
  }

  def findClass(path: String): Option[Class[_]] = {
    if (config hasPath path) {
      val clazz = (getClass() getClassLoader()) loadClass (config getString path)
      Option(clazz)
    } else {
      None
    }
  }

  def getDirectory(path: String) = {
    Directory(config getString path)
  }

}

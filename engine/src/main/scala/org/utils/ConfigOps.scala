package org.utils

import scala.concurrent.duration.TimeUnit
import scala.concurrent.duration.SECONDS
import com.typesafe.config.{ConfigFactory, Config}

import scala.concurrent.duration.FiniteDuration
import scala.language.postfixOps
import scala.reflect.io.{Path, Directory}

class ConfigOps(val config: Config) extends AnyVal {

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

  def findDirectory(path: String): Option[Directory] = {
    if (config hasPath path) {
      Some(Path(config getString path) toDirectory)
    } else {
      None
    }
  }

  def getDirectory(path: String) = {
    Directory(config getString path)
  }

  def bool(path: String) = {
    if (config hasPath path) {
      config getBoolean path
    } else {
      false
    }
  }

  def duration(path:String, timeUnit: TimeUnit = SECONDS):Option[FiniteDuration] = {
    import scala.concurrent.duration.pairLongToDuration
    if (config hasPath path) {
      Some((config getDuration (path, timeUnit)) -> timeUnit)
    } else {
      None
    }
  }

}

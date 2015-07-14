package org.utils

import com.typesafe.config.Config

import scala.language.postfixOps
import scala.reflect.io.Directory

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

  def findDirectory(path: String): Option[Directory] = {
    if (config hasPath path) {
      Some(Directory(config getString path))
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

}

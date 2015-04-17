package org.utils

import com.typesafe.config.Config
import org.system._

import scala.util.Try

/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
class ConfigOps(val config:Config) extends AnyVal {

  def findClass(path:String):Option[Class[_]] = {
    implicit val implicitConfig = config
    Try {
      Class forName lookForValue[String](path)
    } toOption
  }

  def getClass(path:String):Class[_] = {
    findClass(path) getOrElse(sys error(freeText("classNotFound") format path))
  }
}

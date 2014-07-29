package org.albert.actor

import java.nio.file.Path

import akka.actor.Props

import scalaz.Scalaz._
import scalaz._

/**
 * Created by nutscracker on 7/22/2014.
 */

object ConfigReader {

  def apply() = Props(classOf[ConfigReader])

}

class ConfigReader extends RTActor {

  override def receive: Receive = {

    case path:Path => checkConfigFiles(path) fold (prepareConfigFiles(path), NoFoundConfig)

    case default => log.warning(s"cant process message $default")
  }

  def checkConfigFiles(path:Path):Boolean = ???

  def prepareConfigFiles(path:Path):PreparedConfig = ???

  def getConfigByPath(path:Path) = checkConfigFiles(path) fold (prepareConfigFiles(path), NoFoundConfig)

}

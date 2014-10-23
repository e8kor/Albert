package org.albert
package actor

import org.albert.command.WrongPath
import scala.reflect.io.{Directory, File}

/**
 * Created by nutscracker on 7/22/2014.
 */

object ConfigReader extends ActorObject

class ConfigReader extends AlbertActor {

  import scala.reflect.io.Path

  override def receive: Receive = normal

  def normal:Receive = {
    case path:Path =>
      sender() ! (path ifDirectory{extractConfigFilesFromDir(_) map parseFile} getOrElse WrongPath(path))
  }

  def extractConfigFilesFromDir(dir:Directory):Seq[File] = ???

  def parseFile(file:File) = ???


}

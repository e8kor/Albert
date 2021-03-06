package org.utils

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory._

import scala.language.postfixOps
import scala.reflect.io.Directory

class DirectoryOps(val it: Directory) {

  import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE => Created, ENTRY_MODIFY => Modified}

  import org.utils.implicits.dir2DirOps


  def filterDirsBy(fileName: String) = (it dirs) filter (dir => dir findConfig fileName isDefined) toSeq

  def zipDirsByFile(fileName: String): Seq[(Directory, Config)] = (it dirs) filter (dir => dir findConfig fileName isDefined) map (dir => dir -> (dir getConfig fileName)) toSeq

  def findConfig(fileName: String): Option[Config] = (it files) find (file => (file name) equals fileName) map (_ jfile) map parseFile

  def getConfig(fileName: String): Config = (it files) find (file => (file name) equals fileName) map (_ jfile) map parseFile orNull

}

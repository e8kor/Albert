package org.utils

import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.RegistryTypes._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory._

import scala.language.postfixOps
import scala.reflect.io.{Directory, Path}

class DirectoryOps(val it: Directory) {

  import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE => Created, ENTRY_MODIFY => Modified}
  import java.nio.file.WatchEvent.Kind

  import org.implicits.{dir2DirOps, scala2JavaPath}


  def filterDirsBy(fileName: String) = (it dirs) filter (dir => dir findConfig fileName isDefined) toSeq

  def findConfig(fileName: String): Option[Config] = (it files) find (file => (file name) equals fileName) map (_ jfile) map parseFile

  def zipDirsByFile(fileName: String): Seq[(Directory, Config)] = (it dirs) filter (dir => dir findConfig fileName isDefined) map (dir => dir -> (dir getConfig fileName)) toSeq

  def getConfig(fileName: String): Config = (it files) find (file => (file name) equals fileName) map (_ jfile) map parseFile orNull

  def withDirCallback() = callback(Created, it, file => "CRTD" /*logger info(s"monitor: directory created - ${file toString}", file)*/)

  private def callback(kind: Kind[java.nio.file.Path], path: Path, callback: Callback) = RegisterCallback(kind, None, recursive = false, path, callback)

}

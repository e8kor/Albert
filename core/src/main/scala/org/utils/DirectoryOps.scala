package org.utils

import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.RegistryTypes._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory._
//import com.typesafe.scalalogging.LazyLogging
import org.system
import org.system._

import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path}

/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
class DirectoryOps(val it: Directory) /*extends LazyLogging */{

  import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE => Created, ENTRY_MODIFY => Modified}
  import java.nio.file.WatchEvent.Kind

  import org.implicits.{dir2DirOps, file2FileOps, scala2JavaPath}
//  import system.default

//  def hasRequiredFiles: Boolean = requiredFiles() nonEmpty

//  def requiredFiles(): Iterator[File] = (it files) filter (_ isRequiredFile)

//  def hasNoRequiredFiles: Boolean = requiredFiles() isEmpty

//  def rootConfig(): Option[Config] = (it files) find (_ isRootConfig) map (_ jfile) map parseFile

//  def findSubSuites(): Seq[Directory] = (it dirs) filter (_ requiredFiles() nonEmpty) toSeq

  def filterDirsBy(fileName: String) = (it dirs) filter (dir => dir findConfig fileName isDefined) toSeq

  def findConfig(fileName: String): Option[Config] = (it files) find (file => (file name) equals fileName) map (_ jfile) map parseFile

  def zipDirsByFile(fileName: String): Seq[(Directory, Config)] = (it dirs) filter (dir => dir findConfig fileName isDefined) map (dir => dir -> (dir getConfig fileName)) toSeq

  def getConfig(fileName: String): Config = (it files) find (file => (file name) equals fileName) map (_ jfile) map parseFile orNull

//  def suiteConfig(): Option[Config] = (it files) find (file => (file name) equalsIgnoreCase "suite.conf") map (file => parseFile(file jfile))

  def withDirCallback() = callback(Created, it, file => "CRTD"/*logger info(s"monitor: directory created - ${file toString}", file)*/)

  private def callback(kind: Kind[java.nio.file.Path], path: Path, callback: Callback) = RegisterCallback(kind, None, recursive = false, path, callback)

}

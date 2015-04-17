package org.utils

import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.RegistryTypes._
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory._
import com.typesafe.scalalogging.LazyLogging
import org.system
import org.system._

import scala.reflect.io.{Directory, File, Path}

/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
class DirectoryOps(val it: Directory) extends LazyLogging {

  import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE => Created, ENTRY_MODIFY => Modified}
  import java.nio.file.WatchEvent.Kind

  import org.implicits.{dir2DirOps, file2FileOps, scala2JavaPath}
  import system.default

  def requiredFiles(): Iterator[File] = (it files) filter (_ isRequiredFile)

  def hasRequiredFiles: Boolean = requiredFiles() nonEmpty

  def hasNoRequiredFiles: Boolean = requiredFiles() isEmpty

  def rootConfig(): Option[Config] = (it files) find (_ isRootConfig)  map (_ jfile) map parseFile

  def findSubSuites(): Seq[Directory] = (it dirs) filter (_ requiredFiles() nonEmpty) toSeq

  def suiteConfig(): Option[Config] = (it files) find (file => (file name) equalsIgnoreCase default("suiteConfig")) map ( file => parseFile (file jfile))

  def withDirCallback() = callback(Created, it, file => logger info (freeText("dirCreated"), file))

  private def callback(kind:Kind[java.nio.file.Path], path:Path, callback:Callback) = RegisterCallback(kind, None, recursive = false, path, callback)

}

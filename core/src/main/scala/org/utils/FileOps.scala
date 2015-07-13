package org.utils

import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.RegistryTypes._
//import com.typesafe.scalalogging.LazyLogging
import org.system
import org.system._

import scala.language.postfixOps
import scala.reflect.io.{File, Path}

/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
class FileOps(val it: File) /*extends LazyLogging*/ {

  import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE => Created, ENTRY_MODIFY => Modified}
  import java.nio.file.WatchEvent.Kind

//  import system.{requiredFileNames, requiredRootConfig}

//  def isRequiredFile: Boolean = requiredFileNames() exists (((it name) trim) eq)

//  def isRootConfig: Boolean = requiredRootConfig() eq (it name)

  def withFileCallback() = callback(Modified, it, file => "MOD"/*logger info s"monitor: file updated - ${file toString}"*/)

  private def callback(kind:Kind[java.nio.file.Path], path:Path, callback:Callback) = RegisterCallback(kind, None, recursive = false, (path jfile) toPath, callback)

}

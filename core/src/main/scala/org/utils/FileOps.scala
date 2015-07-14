package org.utils

import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.RegistryTypes._

import scala.language.postfixOps
import scala.reflect.io.{File, Path}

class FileOps(val it: File) {

  import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE => Created, ENTRY_MODIFY => Modified}
  import java.nio.file.WatchEvent.Kind


  def withFileCallback() = callback(Modified, it, file => "MOD" /*logger info s"monitor: file updated - ${file toString}"*/)

  private def callback(kind: Kind[java.nio.file.Path], path: Path, callback: Callback) = RegisterCallback(kind, None, recursive = false, (path jfile) toPath, callback)

}

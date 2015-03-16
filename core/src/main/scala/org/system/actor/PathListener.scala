package org.system
package actor

import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE => Created, ENTRY_MODIFY => Modified}
import java.nio.file.WatchEvent.Kind

import akka.actor.{ActorRef, PoisonPill, Terminated}
import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.MonitorActor
import com.beachape.filemanagement.RegistryTypes.Callback
import org.implicits.{PathOps, fileToJFile}
import org.system.api.actor.withProps

import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path}

/**
 * Created by nutscracker on 6/30/2014.
 */
class PathListener(suiteManager: ActorRef, path:Path) extends SystemActor {

  private val fileMonitorActor = (context system) actorOf withProps[MonitorActor](concurrency = 2)
  private val (files, dirs) = path filesAndDirs()

  context watch suiteManager
  ((files map withFileCallback) ++ (dirs map withDirCallback)) foreach (fileMonitorActor ! _)

  override def receive: Receive = normal

  private def normal: Receive = {
    case Terminated(actorRef) ⇒
      log info freeText("terminatingPathListener")
      self ! PoisonPill
  }

  private def callback(kind:Kind[java.nio.file.Path], path:Path, callback:Callback) = RegisterCallback(kind, None, recursive = false, path, callback)

  private def withFileCallback(file: File) = callback(Modified, file, file => log info (freeText("fileUpdated"), file))

  private def withDirCallback(directory: Directory) = callback(Created, directory, file => log info (freeText("dirCreated"), file))

}

package org.system
package actor

import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE => Created, ENTRY_MODIFY => Modified}

import akka.actor.{ActorRef, PoisonPill, Terminated}
import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.MonitorActor
import org.system.implicits.{PathOps, fileToJFile, ReceiveOps}

import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path}

/**
 * Created by nutscracker on 6/30/2014.
 */
class PathListener(suiteManager: ActorRef, path:Path) extends SystemActor {

  private val fileMonitorActor = (context system) actorOf withProps[MonitorActor](concurrency = 2)
  private val (files, dirs) = path filesAndDirs()
  private val callback = RegisterCallback(_, None, false, _, _)

  context watch suiteManager
  ((files map withFileCallback) ++ (dirs map withDirCallback)) foreach (fileMonitorActor ! _)

  override def receive: Receive = normal

  private def normal: Receive = {
    case Terminated(actorRef) ⇒
      log info freeText("terminatingPathListener")
      self ! PoisonPill
  } |: other

  private def withFileCallback(file: File) = callback(Modified, file, file => log info (freeText("fileUpdated"), file))

  private def withDirCallback(directory: Directory) = callback(Created, directory, file => log info (freeText("dirCreated"), file))

}

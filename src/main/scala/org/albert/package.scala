package org

import java.nio.file.{Path, StandardWatchEventKinds}

import akka.actor.ActorSystem
import com.beachape.filemanagement.Messages.RegisterCallback

/**
 * Created by nutscracker on 6/29/2014.
 */
package object albert {

  object Akka {
    implicit val system = ActorSystem("rtp")
  }

  def defaultCallback(implicit path:Path) = Seq(
    RegisterCallback(StandardWatchEventKinds.ENTRY_CREATE, None, true, path, {v => println(s"created file by path $v")}),
    RegisterCallback(StandardWatchEventKinds.ENTRY_DELETE, None, true, path, {v => println(s"deleted file by path $v")}),
    RegisterCallback(StandardWatchEventKinds.ENTRY_MODIFY, None, true, path, {v => println(s"updated file by path $v")})
  )

  case class SuitePath(path:Path)

  case class Start(path:Path)
  case object Restart
}


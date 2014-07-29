package com.rtp.dev.actor

import akka.actor.Props
import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.MonitorActor
import com.rtp.dev.{Restart, Start, SuitePath}

/**
 * Created by nutscracker on 6/30/2014.
 */
object RootExecutor {

  def apply() = Props(classOf[RootExecutor])

}

class RootExecutor extends RTActor {

  implicit val pathReader = context.actorOf(PathReader.apply())

  override def receive: Receive = {
    case Start(path) =>
    case Restart =>
    case SuitePath(path) =>
  }
  
}

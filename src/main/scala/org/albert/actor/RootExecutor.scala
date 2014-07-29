package org.albert.actor

import akka.actor.Props
import org.albert.{Restart, Start, SuitePath}

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

package org.albert.actor

import akka.actor.{ActorRef, Terminated}

/**
 * Created by nutscracker on 7/30/2014.
 */
object Terminator extends ActorObject

class Terminator(app: ActorRef) extends AlbertActor {

  context watch app

  def receive = {
    case Terminated(actorRef) ⇒
      log.info("application supervisor has terminated, shutting down")
      (context system) shutdown()
  }

}

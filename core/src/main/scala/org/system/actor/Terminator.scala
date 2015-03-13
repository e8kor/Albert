package org.system
package actor

import akka.actor.{ActorRef, Terminated}

import scala.language.postfixOps

/**
 * Created by nutscracker on 7/30/2014.
 */
class Terminator(app: ActorRef) extends SystemActor {

  context watch app

  def receive = {
    case Terminated(actorRef) ⇒
      log info freeText("terminatingApplication")
      (context system) shutdown()
  }

}

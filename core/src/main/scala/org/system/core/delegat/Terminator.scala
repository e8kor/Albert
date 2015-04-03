package org.system.core.delegat

import akka.actor.{ActorRef, Terminated}
import org.system._
import org.system.core._

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

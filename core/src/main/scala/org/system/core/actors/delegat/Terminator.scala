package org
package system
package core
package actors
package delegat

import akka.actor.{ActorRef, Terminated}
import com.typesafe.config.Config
import org.system.core.actors.System.SystemActor

import scala.language.postfixOps

class Terminator(app: ActorRef)(implicit config:Config) extends SystemActor {

  context watch app

  def receive = {
    case Terminated(actorRef) ⇒
      log info "root executor terminated - application shutting down"
      (context system) shutdown()
  }

}

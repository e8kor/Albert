package org.system
package actor

import akka.actor.{ActorRef, PoisonPill, Terminated}
import org.system.suite.SuiteConfig

/**
 * Created by nutscracker on 6/30/2014.
 */

class Worker(suiteManager: ActorRef) extends SystemActor {

  context watch suiteManager

  override def receive: Receive = normal

  private def normal: Receive = {
    case Terminated(actorRef) ⇒
      log info freeText("terminatingWorker")
      self ! PoisonPill
    case default:SuiteConfig =>

      ??? // TODO Need to implement and make some diagram of work from
  }

}

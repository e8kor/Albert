package org.albert
package actor

import akka.actor.PoisonPill
import org.albert.command._
import org.albert.message.Message
import org.albert.statuses._
import org.albert.suite.SuiteOrder


/**
 * Created by nutscracker on 8/2/2014.
 */

object SuiteManager extends ActorObject

class SuiteManager extends AlbertActor {

  val listener = (context system) actorOf PathListener()

  val reader = (context system) actorOf ConfigReader()

  val worker = (context system) actorOf Worker()

  private val actor_name = self. path

  override def receive: Receive = initial

  def initial: Receive = {
    case SuitePath(path) =>
        context become configure
        listener ! Listen(path)
        reader ! Read(path)
    case request:WhatIsYourStatus =>
      sender() ! WaitingForPath()
    case default:Message  =>
      log info s" cant handle message $default while initializing"
  }

  def configure: Receive = {
    case ParsedConfig(data) =>
        handle(data)
    case message @ WrongPath(path) =>
      log warning s"trying to configure actor $actor_name with wrong path : $path"
      (context parent) ! message
      self ! PoisonPill
    case request:WhatIsYourStatus =>
      sender() ! ReadingConfig()
    case default:Message  =>
      log info s" cant handle message $default while configuring"
  }

  def wait(slaves:SuiteOrder): Receive = {
    case SuitePath(path) =>
      log warning s"actor $actor_name is not configured but already got another path : $path"
      initiate_restart(slaves)
    case command:Stop =>
      log warning s"initiate stop while waiting for children to execute. actor $actor_name"
      initiate_stop(slaves)
    case request:WhatIsYourStatus =>
      sender() ! WaitingForChildren()
    case default:Message  =>
      log info s" cant handle message $default while restart"
  }

  def work(slaves:SuiteOrder): Receive = {
    case SuitePath(path) =>
      log warning s"bad idea to add new suite path $path while suite is already in work invoked"
    case command:Stop =>
      log warning s"trying to stop actor $actor_name while job in progress"
      initiate_stop(slaves)
    case request:WhatIsYourStatus =>
      sender() ! ImWorking()
    case default:Message  =>
      log info s" cant handle message $default while restart"
  }

  def stop(slaves:SuiteOrder):Receive = {
    case request:WhatIsYourStatus =>
      sender() !TryingToStopMySelf()
    case default:Message =>
      log info s" cant handle message $default while stop"
  }

  def restart(slaves:SuiteOrder):Receive = {
    case request:WhatIsYourStatus =>
      sender() ! ImRestarting()
    case default:Message =>
      log info s" cant handle message $default while restart"
  }

  private def initiate_stop(slaves:SuiteOrder) = {
    context become stop {
      if (slaves isEmpty) {
        self ! PoisonPill
      } else {
        slaves foreach (_ ! Stop())
      }
      slaves
    }
  }

  private def initiate_restart(slaves:SuiteOrder) = {
    context become restart {
      if (slaves isEmpty) {
        self ! PoisonPill
      } else {
        slaves foreach (_ ! Stop())
      }
      slaves
    }
  }

  private def handle(data:Any) = ??? // TODO : implement worker

}

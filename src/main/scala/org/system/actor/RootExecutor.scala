package org.system
package actor

import akka.actor.{PoisonPill, OneForOneStrategy, SupervisorStrategy}
import org.system.command._
import org.system.implicits.{DirectoryOps, PathOps, ReceiveOps}
import org.system.plugin.PluginCommand

import scala.language.postfixOps

/**
 * Created by nutscracker on 6/30/2014.
 */
class RootExecutor extends SystemActor {

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, freeText("tryRestart"), thr)
      SupervisorStrategy restart
  }

  override def receive = normal

  private def normal: Receive = {
    case Start(path) =>
      val dir = path toDirOrParentDir
      if (dir hasNoRequiredFiles) log error freeText("illegalPath")
      else (context system) actorOf(withProps[SuiteManager](dir), dir name)
    case SuiteCompleted =>
      self ! PoisonPill
      log info freeText("allSuitesFinished")
      (context system) shutdown()
      log info freeText("shuttingDown")
    case PluginCommand(pluginType, pluginScript) =>
  } |: other

}
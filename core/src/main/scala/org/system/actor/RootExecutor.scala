package org.system
package actor

import scala.language.postfixOps

import akka.actor.{OneForOneStrategy, PoisonPill, SupervisorStrategy}
import org.system.command._
import org.system.implicits.{DirectoryOps, PathOps}
import org.system.plugin.PluginCommand

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
      val dir = path toDirOrParentDir()
      if (dir hasNoRequiredFiles) log error freeText("illegalPath")
      else (context system) actorOf(withProps[SuiteManager](dir), dir name)
    case SuiteCompleted =>
      self ! PoisonPill
      log info freeText("allSuitesFinished")
      (context system) shutdown()
      log info freeText("shuttingDown")
    case PluginCommand(pluginType, pluginScript) =>
  }

}
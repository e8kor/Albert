package org.system
package actor

import org.system.actor.withProps

import scala.language.postfixOps

import akka.actor.{OneForOneStrategy, PoisonPill, SupervisorStrategy}
import org.system.command._
import org.system.implicits.{DirectoryOps, PathOps}
import org.system.plugin.PluginCommand

import scala.reflect.io.Directory

/**
 * Created by nutscracker on 6/30/2014.
 */
class RootExecutor(rootDir: Directory) extends SystemActor {

  require(rootDir hasRequiredFiles, freeText("illegalPath"))

  (context system) actorOf(withProps[SuiteManager](rootDir), rootDir name)

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, freeText("tryRestart"), thr)
      SupervisorStrategy restart
  }

  override def receive = normal

  private def normal: Receive = {
    case SuiteCompleted =>
      self ! PoisonPill
      log info freeText("allSuitesFinished")
      (context system) shutdown()
      log info freeText("shuttingDown")
    case PluginCommand(pluginType, pluginScript) =>
  }

}
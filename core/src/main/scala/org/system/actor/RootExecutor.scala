package org.system.actor

import akka.actor.{OneForOneStrategy, PoisonPill, SupervisorStrategy}
import org.implicits.DirectoryOps
import org.system._
import org.system.api.actor.withProps
import org.system.command._

import scala.language.postfixOps
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
  }

}
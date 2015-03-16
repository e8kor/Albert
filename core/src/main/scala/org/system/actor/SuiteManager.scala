package org.system
package actor

import akka.actor.{ActorRef, PoisonPill}
import org.implicits.DirectoryOps
import org.system.actor.queue.{SystemProducerSystemActor, SystemConsumerSystemActor}
import org.system.api.actor.withProps
import org.system.command._
import org.system.suite.SuiteConfig

import scala.language.postfixOps
import scala.reflect.io.Directory

/**
 * Created by nutscracker on 8/2/2014.
 */

class SuiteManager(suiteDir: Directory) extends SystemActor {

  val suiteDirs: Seq[Directory] = suiteDir findSlaveSuites()
  suiteDirs foreach (dir => (context system) actorOf(withProps[SuiteManager](dir), dir name))


  (context system) actorOf(withProps[SystemConsumerSystemActor](self), (suiteDir name) concat "Consumer")
  (context system) actorOf(withProps[SystemProducerSystemActor](), (suiteDir name) concat "Producer")

  (context system) actorOf(withProps[PathListener](self, suiteDir), (suiteDir name) concat "Listener")
  (context system) actorOf(withProps[ConfigReader](self, suiteDir), (suiteDir name) concat "ConfigReader")
  (context system) actorOf(withProps[Worker](self), (suiteDir name) concat "Worker")

  override def receive: Receive = configure orElse stop()

  private def configure: Receive = {
    case parsedConfig: SuiteConfig if getActors isEmpty =>
      log info (freeText("noSubSuites"), suiteDir name)
      context become (work orElse stop())
      getWorker foreach (_ ! parsedConfig)
    case parsedConfig: SuiteConfig if getActors nonEmpty =>
      context become (slaves(parsedConfig) orElse stop())
    case WrongSuitePath(path) =>
      self ! PoisonPill
      log error(freeText("wrongPath"), suiteDir name, path)
  }

  private def slaves(parsedConfig: SuiteConfig): Receive = {
    case SuiteCompleted if getActors isEmpty =>
      context become (work orElse stop())
      ((context system) actorOf withProps[Worker](self)) ! parsedConfig
    case SuiteCompleted if (getActors filterNot (_ eq sender())) isEmpty =>
      ((context system) actorOf withProps[Worker](self)) ! parsedConfig
  }

  private def work(): Receive = {
    case WorkCompleted =>
      self ! PoisonPill
      (context parent) ! SuiteCompleted
  }

  private def stop(): Receive = {
    case Stop =>
      log warning(freeText("tryingToStopWhileWork"), suiteDir name)
      (getActors :+ self) foreach (_ ! PoisonPill)
  }

  private def getActors: Seq[ActorRef] = {
    suiteDirs map (_ name) flatMap (context child)
  }

  private def getWorker: Option[ActorRef] = {
    context child ((suiteDir name) concat "Worker")
  }
}

package org.system
package actor

import akka.actor.{ActorRef, PoisonPill}
import org.system.command._
import org.system.implicits.{DirectoryOps, ReceiveOps}

import scala.language.postfixOps
import scala.reflect.io.{Directory, Path}


/**
 * Created by nutscracker on 8/2/2014.
 */

class SuiteManager(suiteDir: Directory) extends SystemActor {

  val suiteDirs: Seq[Directory] = suiteDir findSlaveSuites()
  suiteDirs foreach (dir => (context system) actorOf(withProps[SuiteManager](dir), dir name))

  (context system) actorOf(withProps[PathListener](self, suiteDir), (suiteDir name) concat "Listener")
  (context system) actorOf(withProps[ConfigReader](self, suiteDir), (suiteDir name) concat "ConfigReader")
  (context system) actorOf(withProps[Worker](self), ((self path) name) concat "Worker")

  override def receive: Receive = configure

  private def configure: Receive = {
    case parsedConfig: ParsedConfig if getActors(suiteDirs) isEmpty =>
      context become work
      getWorker(suiteDir) map (_ ! parsedConfig) getOrElse (log error freeText("cantStartWorker"))
    case parsedConfig: ParsedConfig if getActors(suiteDirs) nonEmpty =>
      slaves(parsedConfig)
    case WrongSuitePath(path) =>
      self ! PoisonPill
      log error(freeText("wrongPath"), (self path) name, path)
  } |: stopOrOther()

  private def slaves(parsedConfig: ParsedConfig): Receive = {
    case SuiteCompleted if getActors(suiteDirs) isEmpty =>
      context become work
      ((context system) actorOf withProps[Worker](self)) ! parsedConfig
    case SuiteCompleted if (getActors(suiteDirs) filterNot (_ eq sender())) isEmpty =>
      context become work
      ((context system) actorOf withProps[Worker](self)) ! parsedConfig
    case SuiteCompleted if getActors(suiteDirs) nonEmpty =>
      context become slaves(parsedConfig)
  } |: stopOrOther()

  private def work(): Receive = {
    case WorkCompleted =>
      self ! PoisonPill
      (context parent) ! SuiteCompleted
  } |: stopOrOther()

  private def stopOrOther(): Receive = {
    case Stop =>
      log warning(freeText("tryingToStopWhileWork"), (self path) name)
      context become stopOrOther()
      (getActors(suiteDirs) :+ self) map (_ ! PoisonPill)
  } |: other

  private def getActors(dirs: Seq[Directory]) = {
    dirs map (_ name) map (context child) flatten
  }

  private def getWorker(path: Path): Option[ActorRef] = {
    context child ((path name) concat "Worker")
  }
}

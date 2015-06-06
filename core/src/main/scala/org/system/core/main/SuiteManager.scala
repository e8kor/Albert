package org.system
package core
package main

import akka.actor.{ActorRef, PoisonPill, Props}
import com.beachape.filemanagement.MonitorActor
import com.typesafe.config.Config
import org.implicits.{config2ConfigOps, dir2DirOps, path2PathOps}
import org.system.command.manage._
import org.system.command.status.{ReadingConfig, Status, WaitingForSubSuite, Working}
import org.system.scenario.Scenario

import scala.language.postfixOps
import scala.reflect.io.Directory

/**
 * Created by nutscracker on 8/2/2014.
 */

class SuiteManager(suiteDir: Directory, rootConfig:Config) extends SystemActor {

  type PluginScenario = Scenario[_]

  require(suiteDir suiteConfig() isDefined, freeText("suiteConfigNotFound"))

  suiteDir suiteConfig() map(_ withFallback rootConfig) foreach {
    config =>
      require(config findClass "configReader" isDefined, freeText("configReaderNotFound"))
      require(config findClass "worker" isDefined, freeText("workerNotFound"))

      (context system) actorOf(Props(config getClass "configReader", suiteDir,rootConfig), "ConfigReader")
      (context system) actorOf(Props(config getClass "worker"), "Worker")

      (((context system) actorOf Props(classOf[MonitorActor], 2)) /: (suiteDir getSuiteCallbacks)) {
        case (actor, callback) =>
          actor ! callback
          actor
      }
  }

  (suiteDir findSubSuites()) foreach {
    dir =>
      (context system) actorOf(Props(classOf[SuiteManager], dir, rootConfig), dir name)
  }

  override def receive: Receive = configure orElse stop orElse status(ReadingConfig)

  private def configure: Receive = {
    case parsedConfig: PluginScenario if subSuites isEmpty =>
      log info (freeText("noSubSuites"), suiteDir name)
      context become (work orElse stop orElse status(Working))
      worker foreach (_ ! parsedConfig)
    case parsedConfig: PluginScenario if subSuites nonEmpty =>
      context become (prepare(parsedConfig, subSuites) orElse stop orElse status(WaitingForSubSuite))
    case WrongSuitePath(path) =>
      self ! PoisonPill
      log error(freeText("wrongPath"), suiteDir name, path)
  }

  private def prepare(parsedConfig: PluginScenario, nonCompleted: Seq[ActorRef]): Receive = {
    case SuiteCompleted if nonCompleted forall (_ eq sender()) =>
      context become (work orElse stop orElse status(Working))
      worker foreach (_ ! parsedConfig)
    case SuiteCompleted if nonCompleted exists (_ != sender()) =>
      val minusOne = nonCompleted filterNot (_ eq sender())
      context become (prepare(parsedConfig, minusOne) orElse stop orElse status(WaitingForSubSuite))
  }

  private def work: Receive = {
    case WorkCompleted =>
      self ! PoisonPill
      (context parent) ! SuiteCompleted
  }

  private def stop: Receive = {
    case Stop =>
      log warning(freeText("tryingToStopWhileWork"), suiteDir name)
      (subSuites :+ self) foreach (_ ! PoisonPill)
  }

  private def status(status:Status):Receive = {
    case WhatIsYourStatus => sender() ! status
  }

  private def subSuites: Seq[ActorRef] = {
    suiteDir findSubSuites() map (_ name) flatMap (context child)
  }

  private def worker = {
    context child "Worker"
  }


  private def configReader = {
    context child "ConfigReader"
  }
}

package org.system
package core
package main

import akka.actor.{ActorRef, PoisonPill, Props}
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory._
import org.implicits.{ConfigOps, DirectoryOps}
import org.system.core.delegat.PathListener
import org.system.plugin.model.command.manage.{Stop, SuiteCompleted, WorkCompleted, WrongSuitePath}
import org.system.plugin.model.scenario.Scenario

import scala.language.postfixOps
import scala.reflect.io.Directory

/**
 * Created by nutscracker on 8/2/2014.
 */

class SuiteManager(suiteDir: Directory, rootConfig:Config) extends SystemActor {

  require(suiteDir suiteConfig() isDefined, freeText("suiteConfigNotFound"))

  val suiteConfig = suiteDir suiteConfig() map(_ jfile) map parseFile getOrElse empty() withFallback rootConfig
  val suiteDirs = suiteDir findSlaveSuites()

  require(suiteConfig findClass "configReader" isDefined, freeText("configReaderNotFound"))
  require(suiteConfig findClass "worker" isDefined, freeText("workerNotFound"))

  suiteDirs foreach (dir => (context system) actorOf (Props(classOf[SuiteManager], dir, rootConfig), dir name))

  val pathListener = (context system) actorOf(Props(classOf[PathListener], suiteDir), (suiteDir name) concat "Listener")
  val configReader = (context system) actorOf(Props(suiteConfig getClass "configReader", suiteDir,rootConfig), "ConfigReader")
  val worker = (context system) actorOf(Props(suiteConfig getClass "worker"), "Worker")

  override def receive: Receive = configure orElse stop

  private def configure: Receive = {
    case parsedConfig: Scenario if suites isEmpty =>
      log info (freeText("noSubSuites"), suiteDir name)
      context become (work orElse stop)
      worker ! parsedConfig
    case parsedConfig: Scenario if suites nonEmpty =>
      context become (prepare(parsedConfig) orElse stop)
    case WrongSuitePath(path) =>
      self ! PoisonPill
      log error(freeText("wrongPath"), suiteDir name, path)
  }

  private def prepare(parsedConfig: Scenario): Receive = {
    case SuiteCompleted if suites isEmpty =>
      context become (work orElse stop)
      worker ! parsedConfig
    case SuiteCompleted if (suites filterNot (_ eq sender())) isEmpty =>
      worker ! parsedConfig
  }

  private def work: Receive = {
    case WorkCompleted =>
      self ! PoisonPill
      (context parent) ! SuiteCompleted
  }

  private def stop: Receive = {
    case Stop =>
      log warning(freeText("tryingToStopWhileWork"), suiteDir name)
      (suites :+ self) foreach (_ ! PoisonPill)
  }

  private def suites: Seq[ActorRef] = {
    suiteDirs map (_ name) flatMap (context child)
  }

}

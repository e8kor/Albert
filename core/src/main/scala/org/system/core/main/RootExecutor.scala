package org.system
package core
package main

import akka.actor.{OneForOneStrategy, PoisonPill, Props, SupervisorStrategy}
import com.typesafe.config.ConfigFactory.{empty, parseFile}
import org.implicits.DirectoryOps
import org.system.core.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}
import org.system.plugin.model.command.manage.SuiteCompleted

import scala.language.postfixOps
import scala.reflect.io.Directory

/**
 * Created by nutscracker on 6/30/2014.
 */
class RootExecutor(rootDir: Directory) extends SystemActor {

  require(rootDir rootConfig() isDefined, freeText("illegalPath"))

  val rootConfig = rootDir rootConfig() map (_ jfile) map parseFile getOrElse empty()

  val rootSuiteManager = (context system) actorOf(Props(classOf[SuiteManager], rootDir, rootConfig), rootDir name)
  val commandConsumer = (context system) actorOf(Props(classOf[CommandConsumerSystemActor]), "CommandConsumer")
  val commandProducer = (context system) actorOf(Props(classOf[CommandProducerSystemActor]), "CommandProducer")

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
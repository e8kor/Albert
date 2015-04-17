package org.system
package core
package main

import akka.actor._
import org.implicits.dir2DirOps
import org.system.core.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}
import org.system.command.manage.SuiteCompleted

import scala.language.postfixOps
import scala.reflect.io.Directory

/**
 * Created by nutscracker on 6/30/2014.
 */
class RootExecutor(rootDir: Directory) extends SystemActor {

  require(rootDir rootConfig() isDefined, freeText("illegalPath"))

  rootDir rootConfig() foreach {
    config =>
      (rootDir findSubSuites()) foreach {
        dir =>
          (context system) actorOf(Props(classOf[SuiteManager], dir, config), dir name)
      }
      (context system) actorOf(Props(classOf[CommandConsumerSystemActor]), "CommandConsumer")
      (context system) actorOf(Props(classOf[CommandProducerSystemActor]), "CommandProducer")
  }

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, freeText("tryRestart"), thr)
      SupervisorStrategy restart
  }

  override def receive = awaitCompletion(subSuites)

  private def awaitCompletion(subSuites: Seq[ActorRef]): Receive = {
    case SuiteCompleted if (subSuites filterNot (_ eq sender())) isEmpty =>
      self ! PoisonPill
      log info freeText("allSuitesFinished")
      (context system) shutdown()
      log info freeText("shuttingDown")
    case SuiteCompleted if (subSuites filterNot (_ eq sender())) nonEmpty =>
      context become awaitCompletion(subSuites filterNot (_ eq sender()))
  }

  private def commandProducer = {
    context child "CommandProducer"
  }

  private def commandConsumer = {
    context child "CommandConsumer"
  }

  private def subSuites: Seq[ActorRef] = {
    rootDir findSubSuites() map (_ name) flatMap (context child)
  }

}
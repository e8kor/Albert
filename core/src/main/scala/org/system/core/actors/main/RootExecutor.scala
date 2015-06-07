package org.system
package core
package actors
package main

import akka.actor._
import akka.camel.CamelExtension
import org.implicits.dir2DirOps
import org.system.core.actors.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}
import org.system.command.manage.SuiteCompleted

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.reflect.io.Directory

/**
 * Created by nutscracker on 6/30/2014.
 */
class RootExecutor(rootDir: Directory) extends SystemActor {

  require(rootDir rootConfig() isDefined, freeText("illegalPath"))

  rootDir rootConfig() foreach {
    config =>
      import context.system
      (rootDir findSubSuites()) foreach {
        dir =>
          system actorOf(Props(classOf[SuiteManager], dir, config), dir name)
      }

      system actorOf(Props(classOf[CommandProducerSystemActor]), "CommandProducer")

      val camel = CamelExtension get system
      val endpointRef = system actorOf(Props(classOf[CommandConsumerSystemActor]), "CommandConsumer")
      val endpointF = (camel activationFutureFor endpointRef)(10 seconds, system dispatcher)

      Await ready(endpointF, 10 seconds)

  }

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, freeText("tryRestart"), thr)
      SupervisorStrategy restart
  }

  override def receive = awaitCompletion(subSuites)

  private def awaitCompletion(subSuites: Seq[ActorRef]): Receive = {
    case SuiteCompleted if subSuites forall (_ eq sender()) =>
      self ! PoisonPill
      log info freeText("allSuitesFinished")
      (context system) shutdown()
      log info freeText("shuttingDown")
    case SuiteCompleted if !(subSuites forall (_ eq sender())) =>
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
package org.system
package core
package actors
package main

import akka.actor._
import akka.camel.CamelExtension
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.activemq.camel.component.ActiveMQComponent
import org.implicits.{config2ConfigOps, dir2DirOps}
import org.system.core.actors.System.SystemActor
import org.system.core.actors.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}
import org.system.core.command.manage.{StartSuite, SuiteCompleted}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.reflect.io.Directory

object RootExecutor extends LazyLogging {

  def apply(dir: Directory): RootExecutor = {
    dir findConfig "root.conf" match {
      case Some(rootConf) =>
        RootExecutor(dir, rootConf)
      case None =>
        sys error s"invalid path: root config not found by \n path: ${dir path}"
    }
  }

  def apply(dir: Directory, rootConfig: Config): RootExecutor = {

    val suiteDirs = dir zipDirsByFile "suite.conf"

    logger info
      s"""root executor found suites:
          |${suiteDirs map (_._1) map (_ name) mkString ", "}""".stripMargin

    require(suiteDirs nonEmpty,
      """illegal config: no suites found
        |passed config: $rootCfg""")

    new RootExecutor(dir)(suiteDirs)(rootConfig)
  }
}

class RootExecutor private(rootDirectory: Directory)(suiteDirectories: Seq[(Directory, Config)])(rootConfig: Config) extends SystemActor {

  import context.{become, child, system}

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, "going to restart")
      SupervisorStrategy restart
  }

  if (rootConfig getBoolean "camel_enabled") {
    val camel = CamelExtension(system)

    (camel context) removeComponent (rootConfig getString "mqComponent")
    (camel context) addComponent(rootConfig getString "mqComponent", ActiveMQComponent activeMQComponent)

    val producerRef = context actorOf(Props[CommandProducerSystemActor](CommandProducerSystemActor()(rootConfig)), "CommandProducer")
    val consumerRef = context actorOf(Props[CommandConsumerSystemActor](CommandConsumerSystemActor()(rootConfig)), "CommandConsumer")
    val endpointF = (camel activationFutureFor consumerRef)(10 seconds, system dispatcher)

    Await ready(endpointF, 10 seconds)
  }

  val suiteRefs = suiteDirectories map {
    case (dir, conf) =>
      context actorOf(Props[SuiteManager]( SuiteManager(dir, conf)), dir name)
  }

  if (rootConfig bool "auto_start") {
    log info s"root executor start ${suiteRefs length} suites automatically"
    self ! StartSuite
  } else {
    log info s"root executor initialization completed, waiting to your command"
  }

  override def receive = awaitStart()

  def awaitCompletion(completed: Seq[ActorRef]): Receive = {

    case SuiteCompleted if ((completed :+ sender()) length) equals (suiteRefs length) =>
      log info "root executor: all suites was completed"
      self ! PoisonPill
    case SuiteCompleted =>
      log info "root executor: one of suites completed "
      become(awaitCompletion(completed :+ sender()))
  }

  private def awaitStart(): Receive = {
    case StartSuite =>
      log info s"root executor start ${suiteRefs length} suites on command"
      context become awaitCompletion(Seq())
      suiteRefs foreach (_ ! StartSuite)
  }

  private def commandProducer = child("CommandProducer")

  private def commandConsumer = child("CommandConsumer")

}
package org.system
package core
package actors
package main

import akka.actor._
import akka.camel.CamelExtension
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.activemq.camel.component.ActiveMQComponent
import org.implicits.{config2ConfigOps, dir2DirOps}
import org.system.command.manage.{StartSuite, SuiteCompleted}
import org.system.core.actors.System.SystemActor
import org.system.core.actors.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import scala.reflect.io.Directory

object RootExecutor {

  def apply(rootConfig: Config) = {

    val optRootDir = rootConfig findDirectory "root_directory"

    val suiteDirs = optRootDir map (_ zipDirsByFile "suite.conf") getOrElse Seq()

    require(optRootDir isDefined,
      s"""illegal config: root execution directory not found
          |passed config: $rootConfig""")

    require(suiteDirs nonEmpty,
      """illegal config: no suites found
        |passed config: $rootCfg""")

    new RootExecutor(optRootDir orNull, suiteDirs)(rootConfig)
  }
}

class RootExecutor private(val rootDirectory: Directory, val suiteDirectories: Seq[(Directory, Config)])(implicit val rootConfig: Config) extends SystemActor {

  import context.{become, system}

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, "going to restart")
      SupervisorStrategy restart
  }

  if (rootConfig getBoolean "camel_enabled") {
    prepareCamel(system)
    val camel = CamelExtension get system
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

  //  private def commandProducer = child("CommandProducer")

  //  private def commandConsumer = child("CommandConsumer")

  private def prepareCamel(system: ActorSystem)(implicit config: Config) = {
    val camel = CamelExtension(system)
    (camel context) removeComponent ??? /* TODO : need to provide properly default("mqComponent")*/
    (camel context) addComponent(??? /* TODO : need to provide properly default("mqComponent")*/ , ActiveMQComponent activeMQComponent ??? /* TODO : need to provide properly default("mqURL")*/ )
    camel
  }
}
package org.system
package core
package actors
package main

import akka.actor._
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.implicits.{config2ConfigOps, dir2DirOps}
import org.system.core.actors.System.SystemActor
import org.system.core.actors.track.EventTracker
import org.system.core.command.manage.{StartSuite, SuiteCompleted}
import org.system.core.command.track.PublishStatus

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

    val rootDir = rootConfig findDirectory "root_directory" getOrElse dir

    val suiteDirs = rootDir zipDirsByFile "suite.conf"

    logger info s"root config directory: ${rootDir path} "

    logger info
      s"""root executor found suites:
          |${suiteDirs map (_._1) map (_ name) mkString ", "}""".stripMargin

    require(suiteDirs nonEmpty,
      """illegal config: no suites found
        |passed config: $rootCfg""")

    new RootExecutor(rootDir)(suiteDirs)(rootConfig)
  }

}

class RootExecutor private(rootDirectory: Directory)(suiteDirectories: Seq[(Directory, Config)])(rootConfig: Config) extends SystemActor {
  import scala.concurrent.duration.DurationInt
  import akka.event.EventStream.fromActorSystem

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, "going to restart")
      SupervisorStrategy restart
  }

  val autoStart = rootConfig bool "auto_start"

  val publishInitialDelay = (rootConfig duration "publish_status_initial_delay") getOrElse (1 second)

  val publishInterval = (rootConfig duration "publish_status_interval_delay") getOrElse (5 seconds)

  val suiteRefs = suiteDirectories map {
    case (dir, conf) =>
      context actorOf(Props(SuiteManager(dir, conf)), dir name)
  }

  val eventTracker = context actorOf(Props(EventTracker()), s"${rootDirectory name}.${classOf[EventTracker] getSimpleName}")

  if (autoStart) {
    log info s"root executor start ${suiteRefs length} suites automatically"
    self ! StartSuite
  } else {
    log info s"root executor initialization completed, waiting to your command"
  }

  (((context system) scheduler) schedule (publishInitialDelay, publishInterval)) {
    (context system) publish PublishStatus
  }

  override def receive = awaitStart()

  def awaitCompletion(completed: Seq[ActorRef]): Receive = {
    case SuiteCompleted if ((completed :+ sender()) length) equals (suiteRefs length) =>
      log info "root executor: all suites was completed"
      self ! PoisonPill
    case SuiteCompleted =>
      log info "root executor: one of suites completed "
      context become awaitCompletion(completed :+ sender())
  }

  private def awaitStart(): Receive = {
    case StartSuite =>
      log info s"root executor start ${suiteRefs length} suites on command"
      context become awaitCompletion(Seq())
      suiteRefs foreach (_ ! StartSuite)
  }

  private def commandProducer = context child "CommandProducer"

  private def commandConsumer = context child "CommandConsumer"

}
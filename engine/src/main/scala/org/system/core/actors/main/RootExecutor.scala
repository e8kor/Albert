package org.system
package core
package actors
package main

import akka.actor._
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.system.core.actors.System.SystemActor
import org.system.core.actors.track.EventTracker
import org.system.core.command.jmx.RootExecutorCompleted
import org.system.core.command.manage.{StartSuite, SuiteCompleted}
import org.system.core.command.track.PublishStatus
import org.utils.implicits.{config2ConfigOps, dir2DirOps}

import scala.language.postfixOps
import scala.reflect.io.Directory

// TODO search for common standarts of reports about tests execution

object RootExecutor extends LazyLogging {

  def apply(dir: Directory, hasJMXExecutor: Boolean): RootExecutor = {
    dir findConfig "root.conf" match {
      case Some(rootConf) =>
        RootExecutor(dir, rootConf, hasJMXExecutor)
      case None =>
        sys error s"invalid path: root config not found by \n path: ${dir path}"
    }
  }

  def apply(dir: Directory): RootExecutor = {
    dir findConfig "root.conf" match {
      case Some(rootConf) =>
        RootExecutor(dir, rootConf, hasJMXExecutor = false)
      case None =>
        sys error s"invalid path: root config not found by \n path: ${dir path}"
    }
  }

  def apply(dir: Directory, rootConfig: Config, hasJMXExecutor: Boolean): RootExecutor = {

    val rootDir = rootConfig findDirectory "root_directory" getOrElse dir

    val suiteDirs = rootDir zipDirsByFile "suite.conf"

    logger info s"root config directory: ${rootDir path} "

    logger info
      s"""root executor found suites:
          |${suiteDirs map (_._1) map (_ name) mkString ", "}""".stripMargin

    require(suiteDirs nonEmpty,
      s"""illegal config: no suites found
          |passed config: $rootConfig""")

    new RootExecutor(rootDir)(suiteDirs)(rootConfig)(hasJMXExecutor)
  }

}

class RootExecutor private(rootDirectory: Directory)(suiteDirectories: Seq[(Directory, Config)])(rootConfig: Config)(hasJMXExecutor: Boolean) extends SystemActor {

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration.DurationInt

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, "going to restart")
      SupervisorStrategy restart
  }

  // TODO Configuration options should be parsed to case class to reduce code lines
  private val autoStart = rootConfig bool "auto_start"

  private val publishInitialDelay = (rootConfig duration "publish_status_initial_delay") getOrElse (1 second)

  private val publishInterval = (rootConfig duration "publish_status_interval_delay") getOrElse (5 seconds)

  private val suiteRefs = suiteDirectories map {
    case (dir, conf) =>
      context actorOf(Props(SuiteManager(dir, conf)), dir name)
  }

  private val eventTracker = context actorOf(Props(EventTracker()), s"${rootDirectory name}.${classOf[EventTracker] getSimpleName}")

  if (autoStart || hasJMXExecutor) {
    log info s"root executor start ${suiteRefs length} suites automatically"
    self ! StartSuite
  } else {
    log info s"root executor initialization completed, waiting to your command"
  }

 val statusTask = (((context system) scheduler) schedule (publishInitialDelay, publishInterval)) {
      ((context system) eventStream) publish PublishStatus
    }

  override def preStart(): Unit = {

  }

  override def postStop(): Unit = {
    if (!(statusTask isCancelled)) {
      val state = statusTask cancel()
        log info s" request status scheduled task was canceled ${if (state) "successfully" else "not successfully"} "
    }
  }

  override def receive: Receive = awaitStart()

  def awaitCompletion(completed: Seq[ActorRef]): Receive = {
    case SuiteCompleted if ((completed :+ sender()) length) equals (suiteRefs length) =>
      log info "root executor: all suites was completed"
      if (hasJMXExecutor) {
        (context parent) ! RootExecutorCompleted
      }
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

}
package org.system
package core
package actors
package main

import akka.actor._
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.system.core.actors.System.SystemActor
import org.system.core.actors.track.EventTracker
import org.system.core.command.manage.{StartSuite, SuiteCompleted}
import org.system.core.command.track.PublishStatus
import org.utils.implicits.{config2ConfigOps, dir2DirOps}

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

    logger info
      s"""Setting up root executor by
          |path: ${dir path}
          |config: ${rootConfig toString}""".stripMargin

    new RootExecutor(dir)(rootConfig)
  }

}

class RootExecutor private(configDirectory: Directory)(config: Config) extends SystemActor {

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr: Throwable =>
      log error(thr, "going to restart")
      SupervisorStrategy restart
  }

  import scala.concurrent.ExecutionContext.Implicits.global

  val rootConfig = config asRootConfig configDirectory

  val statusTask = (((context system) scheduler) schedule (rootConfig publishStatusIntialDelay, rootConfig publishStatusInterval)) {
      ((context system) eventStream) publish PublishStatus
    }

  private val suiteRefs = (rootConfig suiteDirs) map {
    case (dir, conf) =>
      context actorOf(Props(SuiteManager(dir, conf)), dir name)
  }

  if (rootConfig autoStart) {
    log info s"root executor start ${suiteRefs length} suites automatically"
    self ! StartSuite
  } else {
    log info s"root executor initialization completed, waiting to your command"
  }

  private val eventTracker = context actorOf(Props(EventTracker()), s"${configDirectory name}.${classOf[EventTracker] getSimpleName}")

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
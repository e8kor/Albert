package org.system
package core
package actors
package main

import akka.actor.{ActorRef, PoisonPill, Props}
import com.beachape.filemanagement.MonitorActor
import com.typesafe.config.Config
import org.implicits.{config2ConfigOps, dir2DirOps, path2PathOps}
import org.system.command.manage._
import org.system.command.status.{ReadingConfig, Status, WaitingForSubSuite, Working}
import org.system.core.actors.System.SystemActor

import scala.language.postfixOps
import scala.reflect.io.Directory

object SuiteManager {

  def apply(suiteDir: Directory, suiteCfg: Config) = {

    require((suiteCfg findClass "runner") isDefined,
      s"""
         |illegal config: suite runner not defined
         |loaded class : ${suiteCfg findClass "runner"}
         |passed dir: ${suiteDir path}
         |passed config: ${suiteCfg toString}
         |""".stripMargin)
    new SuiteManager(suiteDir)(suiteCfg)
  }

}

class SuiteManager private(suiteDir: Directory)(suiteCfg: Config) extends SystemActor {

  import context.{become, parent, system}

  val runnerRef = context actorOf(Props(suiteCfg getClass "runner"), s"${suiteDir name}Runner")

  if (suiteCfg bool "file_watch_enabled") {
    val monitorRef = system actorOf Props(classOf[MonitorActor], 2)

    (suiteDir getSuiteCallbacks) foreach {
      callback =>
        monitorRef ! callback
    }
  }

  val suiteRefs = suiteDir zipDirsByFile "suite.conf" map {
    case (dir, cfg) =>
      context actorOf(Props[SuiteManager](SuiteManager(dir, cfg)), dir name)
  }

  log info s"suite - ${suiteDir name}: initializing ${suiteRefs length} suites"

  override def receive: Receive = awaitStart

  private def awaitStart: Receive = {
    case StartSuite =>
      if (suiteRefs isEmpty) {
        log info "no sub suites detected: execution started"
        runnerRef ! StartWork(suiteDir, suiteCfg)
        become(work orElse stop orElse status(Working))
      } else {
        log info "sub suites detected: await for completion"
        become(prepare(Seq()) orElse stop orElse status(ReadingConfig))
        suiteRefs foreach (_ ! StartSuite)
      }
  }

  private def work: Receive = {
    case WorkCompleted =>
      log info s"work completed: suite - ${suiteDir name}"
      log info
        s"""sending completion status to partner by :
            |path : ${(parent path) toString}
         """.stripMargin
      parent ! SuiteCompleted
      self ! PoisonPill
  }

  private def prepare(completed: Seq[ActorRef]): Receive = {
    case SuiteCompleted if (suiteRefs length) equals (completed :+ sender() length) =>
      log info s"all sub suites complete their work, suite - ${suiteDir name}"
      become(work orElse stop orElse status(Working))
      runnerRef ! StartWork(suiteDir, suiteCfg)
    case SuiteCompleted =>
      log info s"one of sub suites complete their work, suite - ${suiteDir name}"
      become(prepare(completed :+ sender()) orElse stop orElse status(WaitingForSubSuite))
  }

  private def stop: Receive = {
    case Stop =>
      log warning s"trying to stop, suite - ${suiteDir name}"
      (suiteRefs :+ self) foreach (_ ! PoisonPill)
  }

  private def status(status: Status): Receive = {
    case WhatIsYourStatus => sender() ! status
  }

}

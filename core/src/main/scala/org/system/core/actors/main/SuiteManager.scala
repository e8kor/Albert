package org.system
package core
package actors
package main

import akka.actor.{ActorRef, PoisonPill, Props}
import com.beachape.filemanagement.MonitorActor
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.implicits.{config2ConfigOps, dir2DirOps, path2PathOps}
import org.system.api.command.manage._
import org.system.core.actors.System.SystemActor
import org.system.core.command.manage.{StartSuite, Stop, SuiteCompleted, WhatIsYourStatus}
import org.system.core.command.status.Status

import scala.language.postfixOps
import scala.reflect.io.Directory

object SuiteManager extends LazyLogging {

  def apply(suiteDir: Directory, suiteCfg: Config) = {

    require((suiteCfg getClasses "runners") nonEmpty,
      s"""illegal config: suite runner not defined
          |loaded class : ${suiteCfg findClass "runner"}
          |passed dir: ${suiteDir path}
          |passed config: ${suiteCfg toString}""".stripMargin)


    val suiteDirs = suiteDir zipDirsByFile "suite.conf"

    logger info
      s"""suite manager found suites:
          | ${suiteDirs map (_._1) map (_ name) mkString ", "}""".stripMargin

    new SuiteManager(suiteDir)(suiteDirs)(suiteCfg)
  }

}

// TODO possible case that suite will have no runners and sub suites, should such behaviour be allowed ?
class SuiteManager private(suiteDir: Directory)(suiteDirs: Seq[(Directory, Config)])(suiteCfg: Config) extends SystemActor {

  import context.{become, parent}

  // TODO Plugin actors can be mentioned as config files that needed to loaded and passed to plugin
  // TODO Now     - runners: [ "org.system.plugin.info.runner.InfoPrinter" ]
  // TODO Example - runners: [ "simpleInfo.conf" ] or runners: [ "simpleInfo" ]
  // TODO Such configs can be packed with 3rd party jars or added to classpath
  // TODO this approach need to be implemented

  val runnerRefs = ((suiteCfg getClasses "runners") zipWithIndex) map {
    case (clazz, index) =>
      context actorOf(Props(clazz), s"${suiteDir name}.${clazz getSimpleName}.$index")
  } toIndexedSeq

  if (suiteCfg bool "file_watch_enabled") {
    val monitorRef = context actorOf Props(classOf[MonitorActor], 2)

    (suiteDir getSuiteCallbacks) foreach {
      callback =>
        monitorRef ! callback
    }
  }

  val suiteRefs = suiteDirs map {
    case (dir, cfg) =>
      context actorOf(Props[SuiteManager](SuiteManager(dir, cfg)), dir name)
  } toIndexedSeq

  log info s"suite - ${suiteDir name}: initializing ${suiteRefs length} suites"

  override def receive: Receive = awaitStart

  private def awaitStart: Receive = {
    case StartSuite =>
      if (suiteRefs isEmpty) {
        log info "no sub suites detected: execution started"
        become(work(IndexedSeq()))

        // TODO duplication of logic
        if (suiteCfg bool "runners_parallel_execution") {
          runnerRefs foreach (_ ! StartWork(suiteDir, suiteCfg))
        } else {
          (runnerRefs head) ! StartWork(suiteDir, suiteCfg)
        }
      } else {
        log info "sub suites detected: await for completion"
        become(prepare(IndexedSeq()))
        suiteRefs foreach (_ ! StartSuite)
      }
  }

  private def work(completed: IndexedSeq[ActorRef]): Receive = {
    case WorkCompleted if (runnerRefs length) equals ((completed :+ sender()) length) =>
      log info s"runner completed work \n path: ${sender() path}"
      log info s"all runners completed work: suite - ${suiteDir name}"
      log info s"sending completion status to partner by \n path : ${parent path}"
      parent ! SuiteCompleted
      self ! PoisonPill
    case WorkCompleted =>

      val tmp = completed :+ sender()

      log info s"runner completed \n path: ${sender() path}"
      log info s"one more runner completed work: suite - ${suiteDir name}"
      log info
        s"""execution status:
            | total runners - ${runnerRefs length}
            | completed runners - ${tmp length}
            | waiting for - ${runnerRefs diff tmp}""".stripMargin
      if (!(suiteCfg bool "runners_parallel_execution")) {
        ((runnerRefs diff tmp) head) ! StartWork(suiteDir, suiteCfg)
      }
      become(work(tmp))
  }

  private def prepare(completed: IndexedSeq[ActorRef]): Receive = {
    case SuiteCompleted if (suiteRefs length) equals ((completed :+ sender()) length) =>
      log info s"sub suite completed \n path: ${sender() path}"
      log info s"all sub suites complete their work, suite - ${suiteDir name}"
      become(work(IndexedSeq()))
      // TODO duplication of logic
      if (suiteCfg bool "runners_parallel_execution") {
        runnerRefs foreach (_ ! StartWork(suiteDir, suiteCfg))
      } else {
        (runnerRefs head) ! StartWork(suiteDir, suiteCfg)
      }
    case SuiteCompleted =>
      log info s"sub suite completed \n path: ${sender() path}"
      log info s"one of sub suites complete their work, suite - ${suiteDir name}"
      become(prepare(completed :+ sender()))
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

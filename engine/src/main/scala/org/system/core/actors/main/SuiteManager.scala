package org.system
package core
package actors
package main

import akka.actor.{ActorRef, PoisonPill, Props}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.system.plugin.command.manage._
import org.system.core.actors.System.SystemActor
import org.system.core.command.manage.{StartSuite, SuiteCompleted}
import org.system.core.command.track._
import org.system.plugin.command.manage.{StartWork, ExecutionCompleted}
import org.utils.implicits.{config2ConfigOps, dir2DirOps}

import scala.language.postfixOps
import scala.reflect.io.Directory

object SuiteManager extends LazyLogging {

  def apply(suiteDir: Directory, suiteCfg: Config) = {

    val isRunnerConfig = suiteCfg bool "is_runner_config"

    require((if (isRunnerConfig) suiteCfg getPluginsConfig "runners" else suiteCfg getClasses "runners") nonEmpty,
      s"""illegal config: suite runner not defined
          |runner declaration style: ${
        if (suiteCfg bool "is_runner_config")
          s"""runners declared via config files
              |loaded classes : ${suiteCfg getClasses "runners" map (_ getSimpleName) mkString "\n"}""".stripMargin
        else
          s"""runners declared as classes
              |loaded configs :  ${suiteCfg getPluginsConfig "runners" map (_ toString) mkString "\n"}""".stripMargin
      }
          |passed dir: ${suiteDir path}
          |passed config: ${suiteCfg toString}""".stripMargin)

    val suiteDirs = suiteDir zipDirsByFile "suite.conf"

    logger info
      s"""suite manager found suites:
          | ${suiteDirs map (_._1) map (_ name) mkString ", "}""".stripMargin

    new SuiteManager(suiteDir)(suiteDirs)(suiteCfg)
  }

}

class SuiteManager private(suiteDir: Directory)(suiteDirs: Seq[(Directory, Config)])(suiteCfg: Config) extends SystemActor {

  private val isRunnerConfig = suiteCfg bool "is_runner_config"

  private val parallelExecution = suiteCfg bool "runners_parallel_execution"

  private val runnerClasses = if (isRunnerConfig)
    suiteCfg getPluginsConfig "runners" flatMap (_ getClasses "main_class")
  else
    suiteCfg getClasses "runners"

  private val runnerRefs = (runnerClasses zipWithIndex) map {
    case (clazz, index) =>
      context actorOf(Props(clazz), s"${suiteDir name}.${clazz getSimpleName}.$index")
  } toIndexedSeq

  private val suiteRefs = suiteDirs map {
    case (dir, cfg) =>
      context actorOf(Props(SuiteManager(dir, cfg)), dir name)
  } toIndexedSeq

  ((context system) eventStream) subscribe(self, PublishStatus getClass)

  log info s"suite - ${suiteDir name}: initializing ${suiteRefs length} suites: \n ${suiteRefs mkString "\n"}"

  override def receive: Receive = awaitStart

  private def awaitStart: Receive = {

    case StartSuite if suiteRefs nonEmpty =>
      log info "sub suites detected: await for completion"
      context become prepare(IndexedSeq())
      suiteRefs foreach (_ ! StartSuite)

    case StartSuite =>
      log info "no sub suites detected: execution started"
      context become work(IndexedSeq())

      ((context system) eventStream) publish SuiteExecutionStarted(self, suiteDir)
      performExecution(runnerRefs, parallelExecution)

    case PublishStatus =>
      ((context system) eventStream) publish AwaitStart(self, suiteDir)

  }

  private def performExecution(runners: Seq[ActorRef], parallelExecution: Boolean): Unit = {
    if (parallelExecution) {
      (runners par) foreach (_ ! StartWork(suiteDir, suiteCfg))
    } else {
      if (runners nonEmpty) {
        (runners head) ! StartWork(suiteDir, suiteCfg)
      } else {
        log warning
          s""" Looks like no runners defined for testSuite:
                        | path: ${suiteDir path}
                        | config ${suiteCfg toString}
           """.stripMargin
      }
    }
  }

  private def prepare(completed: IndexedSeq[ActorRef]): Receive = {

    case SuiteCompleted if (suiteRefs length) equals ((completed :+ sender()) length) =>
      log info s"sub suite completed \n path: ${sender() path}"
      log info s"all sub suites complete their work, suite - ${suiteDir name}"
      context become work(IndexedSeq())
      ((context system) eventStream) publish SuiteExecutionStarted(self, suiteDir)
      performExecution(runnerRefs, parallelExecution)

    case SuiteCompleted =>
      log info s"sub suite completed \n path: ${sender() path}"
      log info s"one of sub suites complete their work, suite - ${suiteDir name}"

      context become prepare(completed :+ sender())

    case PublishStatus =>
      ((context system) eventStream) publish AwaitSuiteExecution(self, suiteDir)

  }

  private def work(completed: IndexedSeq[ActorRef]): Receive = {

    case state: ExecutionCompleted if (runnerRefs length) equals ((completed :+ sender()) length) =>

      log info s"runner completed work \n path: ${sender() path}"
      log info s"all runners completed work: suite - ${suiteDir name}"
      log info s"sending completion status to parent \n parent path : ${(context parent) path}"

      ((context system) eventStream) publish SuiteExecutionCompleted(self, suiteDir)
      (context parent) ! SuiteCompleted
      self ! PoisonPill

    case state: ExecutionCompleted =>

      val tmp = completed :+ sender()

      log info s"runner completed \n path: ${sender() path}"
      log info s"one more runner completed work: suite - ${suiteDir name}"
      log info
        s"""execution status:
            | total runners - ${runnerRefs length}
            | completed runners - ${tmp length}
            | waiting for - ${runnerRefs diff tmp}""".stripMargin

      context become work(tmp)

      ((context system) eventStream) publish RunnerExecutionCompleted(self, suiteDir)
      if (!parallelExecution) {
        ((runnerRefs diff tmp) head) ! StartWork(suiteDir, suiteCfg)
      }

    case PublishStatus =>
      ((context system) eventStream) publish AwaitRunnersExecution(self, suiteDir)

  }

}

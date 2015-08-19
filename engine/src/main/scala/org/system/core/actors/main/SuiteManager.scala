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
    new SuiteManager(suiteDir)(suiteCfg)
  }

}

class SuiteManager private(suite: Directory)(config: Config) extends SystemActor {

  val suiteConfig = config asSuiteConfig suite

  private val runnerRefs = ((suiteConfig pluginClasses) zipWithIndex) map {
    case (clazz, index) =>
      context actorOf(Props(clazz), s"${suite name}.${clazz getSimpleName}.$index")
  } toIndexedSeq

  private val suiteRefs = (suiteConfig suiteDirs) map {
    case (dir, cfg) =>
      context actorOf(Props(SuiteManager(dir, cfg)), dir name)
  } toIndexedSeq

  ((context system) eventStream) subscribe(self, PublishStatus getClass)

  log info s"suite - ${suite name}: initializing ${suiteRefs length} suites: \n ${suiteRefs mkString "\n"}"

  override def receive: Receive = awaitStart

  private def awaitStart: Receive = {

    case StartSuite if suiteRefs nonEmpty =>
      log info "sub suites detected: await for completion"
      context become prepare(IndexedSeq())
      suiteRefs foreach (_ ! StartSuite)

    case StartSuite =>
      log info "no sub suites detected: execution started"
      context become work(IndexedSeq())

      ((context system) eventStream) publish SuiteExecutionStarted(self, suite)
      performExecution(runnerRefs, suiteConfig runnerParallelExecution)

    case PublishStatus =>
      ((context system) eventStream) publish AwaitStart(self, suite)

  }

  private def performExecution(runners: Seq[ActorRef], parallelExecution: Boolean): Unit = {
    if (parallelExecution) {
      (runners par) foreach (_ ! StartWork(suite, config))
    } else {
      if (runners nonEmpty) {
        (runners head) ! StartWork(suite, config)
      } else {
        log warning
          s""" Looks like no runners defined for testSuite:
                        | path: ${suite path}
                        | config ${config toString}
           """.stripMargin
      }
    }
  }

  private def prepare(completed: IndexedSeq[ActorRef]): Receive = {

    case SuiteCompleted if (suiteRefs length) equals ((completed :+ sender()) length) =>
      log info s"sub suite completed \n path: ${sender() path}"
      log info s"all sub suites complete their work, suite - ${suite name}"
      context become work(IndexedSeq())
      ((context system) eventStream) publish SuiteExecutionStarted(self, suite)
      performExecution(runnerRefs, suiteConfig runnerParallelExecution)

    case SuiteCompleted =>
      log info s"sub suite completed \n path: ${sender() path}"
      log info s"one of sub suites complete their work, suite - ${suite name}"

      context become prepare(completed :+ sender())

    case PublishStatus =>
      ((context system) eventStream) publish AwaitSuiteExecution(self, suite)

  }

  private def work(completed: IndexedSeq[ActorRef]): Receive = {

    case state: ExecutionCompleted if (runnerRefs length) equals ((completed :+ sender()) length) =>

      log info s"runner completed work \n path: ${sender() path}"
      log info s"all runners completed work: suite - ${suite name}"
      log info s"sending completion status to parent \n parent path : ${(context parent) path}"

      ((context system) eventStream) publish SuiteExecutionCompleted(self, suite)
      (context parent) ! SuiteCompleted
      self ! PoisonPill

    case state: ExecutionCompleted =>

      val tmp = completed :+ sender()

      log info s"runner completed \n path: ${sender() path}"
      log info s"one more runner completed work: suite - ${suite name}"
      log info
        s"""execution status:
            | total runners - ${runnerRefs length}
            | completed runners - ${tmp length}
            | waiting for - ${runnerRefs diff tmp}""".stripMargin

      context become work(tmp)

      ((context system) eventStream) publish RunnerExecutionCompleted(self, suite)
      if (!(suiteConfig runnerParallelExecution)) {
        ((runnerRefs diff tmp) head) ! StartWork(suite, config)
      }

    case PublishStatus =>
      ((context system) eventStream) publish AwaitRunnersExecution(self, suite)

  }

}

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
import org.system.core.command.manage.{StartSuite, SuiteCompleted}
import org.system.core.command.track._

import scala.language.postfixOps
import scala.reflect.io.Directory

object SuiteManager extends LazyLogging {
  
  def apply(suiteDir: Directory, suiteCfg: Config) = {

    // TODO Configuration can be parsed to config case class and provided to suite
    // TODO Didnt manage yet how to do it smoothly

    require((suiteCfg getClasses "runners") nonEmpty,
      s"""illegal config: suite runner not defined
          |loaded classes : ${suiteCfg getClasses "runners" map (_ getSimpleName) mkString ", "}
          |passed dir: ${suiteDir path}
          |passed config: ${suiteCfg toString}""".stripMargin)

    val suiteDirs = suiteDir zipDirsByFile "suite.conf"

    logger info
      s"""suite manager found suites:
          | ${suiteDirs map (_._1) map (_ name) mkString ", "}""".stripMargin

    new SuiteManager(suiteDir)(suiteDirs)(suiteCfg)
  }

}

// TODO SuiteManager can be persistent actor
// TODO possible case that suite will have no runners and sub suites, should such behaviour be allowed ?
class SuiteManager private(suiteDir: Directory)(suiteDirs: Seq[(Directory, Config)])(suiteCfg: Config) extends SystemActor {

  import akka.event.EventStream.fromActorSystem

  // TODO Plugin actors can be mentioned as config files that needed to loaded and passed to plugin
  // TODO Now     - runners: [ "org.system.plugin.info.runner.InfoPrinter" ]
  // TODO Example - runners: [ "simpleInfo.conf" ] or runners: [ "simpleInfo" ]
  // TODO Such configs can be packed with 3rd party jars or added to classpath
  // TODO this approach need to be implemented
  // ---------------
  // TODO usability of plugins that declared via config files is approach to use plugin actors via remote connection

  private val parallelExecution = suiteCfg bool "runners_parallel_execution"

  private val fileWatchEnabled = suiteCfg bool "file_watch_enabled"

  private val runnerClazzes = suiteCfg getClasses "runners"  // TODO need to check that class is subclass of akka.actor.Actor

  private val runnerRefs = (runnerClazzes zipWithIndex) map {
    case (clazz, index) =>
      context actorOf(Props(clazz), s"${suiteDir name}.${clazz getSimpleName}.$index")
  } toIndexedSeq

  // TODO may be its good to have lazy suites initialization
  private val suiteRefs = suiteDirs map {
    case (dir, cfg) =>
      context actorOf(Props(SuiteManager(dir, cfg)), dir name)
  } toIndexedSeq

  if (fileWatchEnabled) {
    val monitorRef = context actorOf MonitorActor(5)

    (suiteDir getSuiteCallbacks) foreach {
      callback =>
        monitorRef ! callback
    }
  }

  (context system) subscribe(self, PublishStatus getClass)

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

      // TODO duplication of logic
      (context system) publish SuiteExecutionStarted(self, suiteDir)
      if (parallelExecution) {
        (runnerRefs par) foreach (_ ! StartWork(suiteDir, suiteCfg))
      } else {
        (runnerRefs head) ! StartWork(suiteDir, suiteCfg)
      }

    case PublishStatus =>
      (context system) publish AwaitStart(self, suiteDir)
  }

  private def prepare(completed: IndexedSeq[ActorRef]): Receive = {
    case SuiteCompleted if (suiteRefs length) equals ((completed :+ sender()) length) =>
      log info s"sub suite completed \n path: ${sender() path}"
      log info s"all sub suites complete their work, suite - ${suiteDir name}"
      context become work(IndexedSeq())
      // TODO duplication of logic
      (context system) publish SuiteExecutionStarted(self, suiteDir)
      if (parallelExecution) {
        (runnerRefs par) foreach (_ ! StartWork(suiteDir, suiteCfg))
      } else {
        (runnerRefs head) ! StartWork(suiteDir, suiteCfg)
      }

    case SuiteCompleted =>

      log info s"sub suite completed \n path: ${sender() path}"
      log info s"one of sub suites complete their work, suite - ${suiteDir name}"

      context become prepare(completed :+ sender())

    case PublishStatus =>
      (context system) publish AwaitSuiteExecution(self, suiteDir)
  }

  // TODO need to implement fail test suite on single test failed as config option
  private def work(completed: IndexedSeq[ActorRef]): Receive = {
    case ExecutionCompleted if (runnerRefs length) equals ((completed :+ sender()) length) =>

      log info s"runner completed work \n path: ${sender() path}"
      log info s"all runners completed work: suite - ${suiteDir name}"
      log info s"sending completion status to parent \n parent path : ${(context parent) path}"

      (context system) publish SuiteExecutionCompleted(self, suiteDir)
      (context parent) ! SuiteCompleted
      self ! PoisonPill

    case ExecutionCompleted =>

      val tmp = completed :+ sender()

      log info s"runner completed \n path: ${sender() path}"
      log info s"one more runner completed work: suite - ${suiteDir name}"
      log info
        s"""execution status:
            | total runners - ${runnerRefs length}
            | completed runners - ${tmp length}
            | waiting for - ${runnerRefs diff tmp}""".stripMargin

      context become work(tmp)

      (context system) publish RunnerExecutionCompleted(self, suiteDir)
      if (!(suiteCfg bool "runners_parallel_execution")) {
        ((runnerRefs diff tmp) head) ! StartWork(suiteDir, suiteCfg)
      }

    case PublishStatus =>
      (context system) publish AwaitRunnersExecution(self, suiteDir)
  }

  //  private def stop: Receive = {
  //    case Stop =>
  //      log warning s"trying to stop, suite - ${suiteDir name}"
  //      (suiteRefs :+ self) foreach (_ ! PoisonPill)
  //  }
  //
  //  private def execute(refs: Seq[ActorRef], request: Request, parallel: Boolean) = {
  //    import akka.pattern._
  //
  //    implicit val infinite = Timeout durationToTimeout FiniteDuration(10, TimeUnit MINUTES)
  //
  //    (if (parallel) {
  //      runnerRefs par
  //    } else {
  //      runnerRefs seq
  //    }) map {
  //      ref =>
  //        pipe(ref ask request) to(self, ref)
  //    }
  //
  //  }
  //
  //  private def publish(tracking: Tracking) = {
  //    ((context system) eventStream) publish tracking
  //  }

}

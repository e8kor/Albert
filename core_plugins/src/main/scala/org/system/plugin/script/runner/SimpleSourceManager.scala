package org.system.plugin.script.runner

import akka.actor.PoisonPill
import com.typesafe.config.ConfigFactory
import org.system.api.command.manage.{ExecutionCompleted, ExecutionFailed, StartWork}
import org.system.plugin.runner.PluginRunnerActor

import scala.language.postfixOps
import scala.reflect.io.Directory

// TODO this plugin need to be developed
class SimpleSourceManager extends PluginRunnerActor {

  override def receive: Receive = {
    case StartWork(dir, cfg) =>

      (dir files) find (file => (file name) equals "script_runner_setup.conf") map {
        file =>
          ConfigFactory parseFile (file jfile) withFallback cfg
      } map {
        runnerConfig =>
          import com.googlecode.scalascriptengine._

          val src = ScalaScriptEngine onChangeRefresh (Directory(runnerConfig getString "script_src_home") jfile)

          log info s"simple source manager updated  : ${src versionNumber}"

          true
      } match {
        case Some(results) =>
          log info s"simple source manager finished execution"
          sender() ! ExecutionCompleted
        case None =>
          log info s"simple source manager failed to execute, some configs or scripts not found"
          sender() ! ExecutionFailed
      }
      self ! PoisonPill
  }

}

package org.system.plugin.script.runner

import akka.actor.PoisonPill
import com.typesafe.config.ConfigFactory
import org.system.plugin.command.manage.StartWork
import org.system.plugin.command.manage.{ExecutionFailed, ExecutionSuccessfullyCompleted}
import org.system.plugin.runner.PluginRunnerActor

import scala.language.postfixOps
import scala.reflect.io.Directory

class SimpleScripting extends PluginRunnerActor {

  override def receive: Receive = {
    case StartWork(dir, cfg) =>

      (dir files) find (file => (file name) equals "script_runner_setup.conf") map {
        file =>
          ConfigFactory parseFile (file jfile)
      } map {
        runnerConfig =>
          val scriptsToExecute = runnerConfig getStringList "scripts"

          val srcDir = Directory(runnerConfig getString "script_src_home")

          (srcDir files) filter (file => scriptsToExecute contains (file name)) map {
            file =>
              file lines() mkString "\n"
          }

      } map {
        scripts =>
          scripts map {
            script =>
              import com.googlecode.scalascriptengine._

              ((EvalCode withoutArgs)[Unit](script) newInstance)()

              true
          }
      } match {
        case Some(results) =>
          log info s"simple script runner finished execution: ${results length} executed"
          sender() ! ExecutionSuccessfullyCompleted
        case None =>
          log info s"simple script failed to execute, some configs or scripts not found"
          sender() ! ExecutionFailed
      }

      self ! PoisonPill
  }

}

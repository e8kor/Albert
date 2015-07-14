package org.system.plugin.script.runner

import com.typesafe.config.ConfigFactory
import org.system.api.command.manage.{StartWork, WorkCompleted}
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
          import com.googlecode.scalascriptengine._
          scripts map {
            script =>
              ((EvalCode withoutArgs) [Unit] (script) newInstance)()
          }
      } match {
        case Some(results) =>
          log info s"simple script runner finished execution: ${results length} executed"
        case None =>
          log info s"simple script failed to execute, some configs or scripts not found"
      }

      sender() ! WorkCompleted
  }

}
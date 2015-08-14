package org.system.plugin.shellscript.runner

import akka.actor.PoisonPill
import com.typesafe.config.ConfigFactory
import org.system.api.command.manage.{ExecutionFailed, ExecutionSuccessfullyCompleted, StartWork}
import org.system.plugin.runner.PluginRunnerActor

import scala.language.postfixOps

class ShellScriptExecutor extends PluginRunnerActor {

  import scala.sys.process._

  override def receive: Receive = {
    case StartWork(dir, cfg) =>
      val scriptExecutorConfig = (dir files) find {
        file =>
          (file name) equals "shell_executor.conf"
      } map (_ jfile) map (ConfigFactory parseFile) getOrElse {
        cfg
      }

      require(scriptExecutorConfig hasPath "script_path", "Script path is not defined in plugin configuration")

      val procLogger = ProcessLogger {
        str =>
          log info s"[ProcessLogger] $str"
      }

      val command = scriptExecutorConfig getString "script_path"

      val outputStream = command lineStream

      val exitCode = command !< procLogger

      outputStream foreach {
        str =>
          log info s"[OutputStream] $str"
      }

      log info s"Exit code : $exitCode"

      if (exitCode == 0) {
        sender() ! ExecutionSuccessfullyCompleted
      } else {
        sender() ! ExecutionFailed
      }

      self ! PoisonPill
  }

}

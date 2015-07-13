package org.system.plugin.script.runner

import org.system.command.manage.{StartWork, WorkCompleted}
import org.system.plugin.runner.PluginRunnerActor

import scala.language.postfixOps

class SimpleInfo extends PluginRunnerActor {

  override def receive: Receive = {
    case StartWork(dir, cfg) =>
      log info
        s"""
           |ScriptRunner created:
           |path : ${dir path}
           |config : ${cfg toString}
        """.stripMargin

      sender() ! WorkCompleted
  }

}

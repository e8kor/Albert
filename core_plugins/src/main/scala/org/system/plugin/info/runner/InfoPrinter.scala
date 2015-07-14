package org.system.plugin.info.runner

import org.system.api.command.manage.{StartWork, WorkCompleted}
import org.system.plugin.runner.PluginRunnerActor

import scala.language.postfixOps

class InfoPrinter extends PluginRunnerActor {

  override def receive: Receive = {
    case StartWork(dir, cfg) =>
      log info
        s"""InfoPrinter created:
            |path : ${dir path}
            |config : ${cfg toString}""".stripMargin

      sender() ! WorkCompleted
  }

}

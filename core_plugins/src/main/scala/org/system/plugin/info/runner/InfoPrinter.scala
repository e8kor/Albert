package org.system.plugin.info.runner

import akka.actor.PoisonPill
import org.system.api.command.manage.{ExecutionCompleted, StartWork}
import org.system.plugin.runner.PluginRunnerActor

import scala.language.postfixOps

class InfoPrinter extends PluginRunnerActor {

  override def receive: Receive = {
    case StartWork(dir, cfg) =>
      log info
        s"""InfoPrinter created:
            |path : ${dir path}
            |config : ${cfg toString}""".stripMargin

      sender() ! ExecutionCompleted
      self ! PoisonPill
  }

}

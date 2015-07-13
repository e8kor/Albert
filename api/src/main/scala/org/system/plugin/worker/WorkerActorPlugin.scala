package org.system.plugin.worker

import akka.actor.{ActorLogging, Actor}
import org.system.scenario.Scenario

trait WorkerActorPlugin extends Actor with ActorLogging {

  type PluginScenario <: Scenario[PluginScenario]

}

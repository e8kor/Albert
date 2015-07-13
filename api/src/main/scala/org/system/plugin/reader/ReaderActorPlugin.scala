package org.system.plugin.reader

import akka.actor.{ActorLogging, Actor}
import org.system.scenario.Scenario

trait ReaderActorPlugin extends Actor with ActorLogging {

  type PluginScenario <: Scenario[PluginScenario]

}


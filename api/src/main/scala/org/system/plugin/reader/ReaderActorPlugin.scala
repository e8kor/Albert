package org.system.plugin.reader

import akka.actor.{ActorLogging, Actor}
import org.system.scenario.Scenario

/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
trait ReaderActorPlugin extends Actor with ActorLogging {

  type PluginScenario <: Scenario[PluginScenario]

}


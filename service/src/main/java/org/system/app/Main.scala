package org.system.app

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

object Main extends BootstrapComponent with App {

   override val actorSystem = ActorSystem create "Albert"

   override val config: Config = ConfigFactory load()

   startup()
 }

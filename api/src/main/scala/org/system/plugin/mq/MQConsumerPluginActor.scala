package org.system.plugin.mq

import akka.actor.{ActorLogging, Actor}
import akka.camel.Consumer

trait MQConsumerPluginActor extends Actor with ActorLogging with Consumer


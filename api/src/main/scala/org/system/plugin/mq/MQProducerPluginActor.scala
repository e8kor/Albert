package org.system.plugin.mq

import akka.actor.{ActorLogging, Actor}
import akka.camel.Producer

trait MQProducerPluginActor extends Actor with ActorLogging with Producer
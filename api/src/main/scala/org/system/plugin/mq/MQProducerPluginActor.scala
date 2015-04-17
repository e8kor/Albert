package org.system.plugin.mq

import akka.actor.{ActorLogging, Actor}
import akka.camel.Producer

/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
trait MQProducerPluginActor extends Actor with ActorLogging with Producer
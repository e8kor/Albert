package org.system.plugin.mq

import akka.actor.{ActorLogging, Actor}
import akka.camel.Consumer

/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
trait MQConsumerPluginActor extends Actor with ActorLogging with Consumer


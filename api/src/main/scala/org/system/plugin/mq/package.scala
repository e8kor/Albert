package org.system.plugin

import akka.actor.{ActorLogging, Actor}
import akka.camel.{Consumer, Producer}

/**
 * Created by evgeniikorniichuk on 24/03/15.
 */
package object mq {

  trait MQProducerPluginActor extends Actor with ActorLogging with Producer

  trait MQConsumerPluginActor extends Actor with ActorLogging with Consumer
  
}

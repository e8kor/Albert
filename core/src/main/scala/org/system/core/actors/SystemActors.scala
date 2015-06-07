package org.system.core.actors

/**
 * Created by evgeniikorniichuk on 07/06/15.
 */

import akka.actor.{Actor, ActorLogging}

trait SystemActor extends Actor with ActorLogging

import akka.camel.Consumer

trait SystemConsumerActor extends SystemActor with Consumer

import akka.camel.Producer

trait SystemProducerActor extends SystemActor with Producer

package org.system

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object core {

  import akka.actor.{Actor, ActorLogging}
  import akka.camel.{Consumer, Producer}

  trait SystemActor extends Actor with ActorLogging

  trait SystemProducerActor extends SystemActor with Producer

  trait SystemConsumerActor extends SystemActor with Consumer

}
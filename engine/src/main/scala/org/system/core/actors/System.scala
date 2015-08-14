package org.system.core.actors

object System {

  import akka.actor.{Actor, ActorLogging}

  abstract class SystemActor extends Actor with ActorLogging

  import akka.camel.Consumer

  abstract class SystemConsumerActor extends SystemActor with Consumer

  import akka.camel.Producer

  abstract class SystemProducerActor extends SystemActor with Producer

}

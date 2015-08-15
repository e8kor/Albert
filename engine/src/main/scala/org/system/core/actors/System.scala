package org.system.core.actors

object System {

  import akka.actor.{Actor, ActorLogging}

  abstract class SystemActor extends Actor with ActorLogging

}

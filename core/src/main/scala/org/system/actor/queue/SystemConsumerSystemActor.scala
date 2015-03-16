package org.system.actor.queue

import akka.actor.ActorRef
import akka.camel.CamelMessage
import org.system._
import org.system.actor.SystemConsumerActor

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class SystemConsumerSystemActor(
                                val proxy:ActorRef,
                                override val endpointUri: String = default("consumerEndpointUrl")
                                ) extends SystemConsumerActor {

  override def receive: Receive = normal

  private def normal: Receive = {
    case message: CamelMessage =>
      log info (message toString())
      proxy ! message
    case default => sys error freeText("unsupportedType")
  }

}

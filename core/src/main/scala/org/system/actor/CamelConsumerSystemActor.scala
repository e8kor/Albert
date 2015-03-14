package org.system
package actor

import akka.actor.ActorRef
import akka.camel.CamelMessage

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class CamelConsumerSystemActor(
                                val proxy:ActorRef,
                                override val endpointUri: String = default("consumerEndpointUrl")
                                ) extends SystemCamelConsumerActor {

  override def receive: Receive = normal

  private def normal: Receive = {
    case message: CamelMessage =>
      log info (message toString())
      proxy ! message
    case default => sys error freeText("unsupportedType")
  }

}

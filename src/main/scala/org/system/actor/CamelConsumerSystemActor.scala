package org.system
package actor

import akka.camel.CamelMessage

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class CamelConsumerSystemActor extends SystemCamelConsumerActor {

  override val endpointUri: String = default("consumerEndpointUrl")

  override def receive: Receive = normal

  private def normal:Receive = {
    case message:CamelMessage => ???
    case default => sys error freeText("unsupportedType")
  }

}

package org.system
package actor

import akka.camel.CamelMessage

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class CamelProducerSystemActor extends SystemCamelProducerActor {

  override val queueType: String = ???

  override val endpointUri: String = default("producerEndpointUrl") formatted queueType

}

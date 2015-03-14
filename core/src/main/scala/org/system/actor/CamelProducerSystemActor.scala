package org.system
package actor

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class CamelProducerSystemActor(
                                override val endpointUri: String = default("producerEndpointUrl")
                                ) extends SystemCamelProducerActor

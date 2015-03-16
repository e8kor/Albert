package org.system.actor.queue

import org.system._
import org.system.actor.SystemProducerActor

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class SystemProducerSystemActor(
                                override val endpointUri: String = default("producerEndpointUrl")
                                ) extends SystemProducerActor

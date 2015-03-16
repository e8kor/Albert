package org.system.actor.queue

import org.system._
import org.system.actor.SystemProducerActor

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class CommandProducerSystemActor(
                                override val endpointUri: String = default("commandProducerUrl")
                                ) extends SystemProducerActor

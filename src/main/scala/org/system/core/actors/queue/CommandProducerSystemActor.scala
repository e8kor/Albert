package org.system.core.actors.queue

import com.typesafe.config.Config
import org.system.core.actors.System.SystemProducerActor

object CommandProducerSystemActor {
  def apply(config: Config) = {
    new CommandProducerSystemActor(config getString "commandProducerUrl")(config)
  }
}

// TODO This guy implementation not finished yet
class CommandProducerSystemActor private(
                                          override val endpointUri: String
                                          )(implicit config: Config) extends SystemProducerActor

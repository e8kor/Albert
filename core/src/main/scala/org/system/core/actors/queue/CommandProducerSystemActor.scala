package org.system.core.actors.queue

import com.typesafe.config.Config
import org.system.core.actors.SystemProducerActor

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class CommandProducerSystemActor(
                                override val endpointUri: String
                                ) extends SystemProducerActor {

  def this(suiteConfig:Config) {
    this(suiteConfig getString "commandProducerUrl")
  }

}

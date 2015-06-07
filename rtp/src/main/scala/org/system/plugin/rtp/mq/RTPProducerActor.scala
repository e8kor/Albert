package org.system.plugin.rtp.mq

import com.typesafe.config.Config
import org.system.plugin.mq.MQProducerPluginActor

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class RTPProducerActor(override val endpointUri: String) extends MQProducerPluginActor {

  def this(suiteConfig: Config) {
    this(suiteConfig getString "producerEndpointUrl")
  }

}

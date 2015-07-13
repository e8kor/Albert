package org.system.plugin.rtp.mq

import com.typesafe.config.Config
import org.system.plugin.mq.MQProducerPluginActor

class RTPProducerActor(override val endpointUri: String) extends MQProducerPluginActor {

  def this(suiteConfig: Config) {
    this(suiteConfig getString "producerEndpointUrl")
  }

}

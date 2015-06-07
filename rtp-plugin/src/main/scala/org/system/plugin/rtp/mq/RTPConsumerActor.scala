package org.system
package plugin.rtp.mq

import akka.actor.Actor.Receive
import akka.camel.CamelMessage
import com.typesafe.config.Config
import org.system.plugin.mq.MQConsumerPluginActor

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class RTPConsumerActor(override val endpointUri: String) extends MQConsumerPluginActor {

  def this(suiteConfig: Config) {
    this(suiteConfig getString "consumerEndpointUrl")
  }

  override def receive: Receive = normal

  private def normal: Receive = {
    case message: CamelMessage =>
      log info (message toString())
      (context parent) ! message
    case default => sys error freeText("unsupportedType")
  }

}

package org
package system
package core
package actors
package queue

import akka.camel.CamelMessage
import com.typesafe.config.Config
import org.system.core.actors.System.SystemConsumerActor

import scala.language.postfixOps

object CommandConsumerSystemActor {

  def apply(config: Config) = {
    new CommandConsumerSystemActor(config getString "commandConsumerUrl")
  }

}

class CommandConsumerSystemActor private(
                                          override val endpointUri: String
                                          )(implicit config: Config) extends SystemConsumerActor {

  override def receive: Receive = normal

  private def normal: Receive = {
    case message: CamelMessage =>
      log info (message toString())
      (context parent) ! message
    case default => sys error s"message not supported - $default"
  }

}

package org.system.core.queue

import akka.camel.CamelMessage
import com.typesafe.config.Config
import org.system._
import org.system.core.SystemConsumerActor

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 08.11.14.
 */
class CommandConsumerSystemActor(
                                override val endpointUri: String
                                ) extends SystemConsumerActor {

  def this(suiteConfig:Config) {
    this(suiteConfig getString "commandConsumerUrl")
  }

  override def receive: Receive = normal

  private def normal: Receive = {
    case message: CamelMessage =>
      log info (message toString())
      (context parent) ! message
    case default => sys error freeText("unsupportedType")
  }

}

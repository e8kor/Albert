package org.system
package test

import akka.actor.Props
import akka.camel.CamelMessage
import akka.testkit.TestProbe
import org.system.core.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}
import org.system.test.spec.{Message, SystemActorSpec}

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 13/03/15.
 */
class CamelSystemTestSuite extends SystemActorSpec {

  "A Producer and consumer" must {

    val probe  = TestProbe()
    val consumer = system actorOf(Props(classOf[CommandConsumerSystemActor],probe ref, default("consumerEndpointUrl")), "Consumer")
    val producer = system actorOf(Props(classOf[CommandProducerSystemActor],default("producerEndpointUrl")), "Producer")

    "send and receive a simple message" in {
      producer ! Message("first")

      (probe expectMsgType)[CamelMessage]
    }
  }
}

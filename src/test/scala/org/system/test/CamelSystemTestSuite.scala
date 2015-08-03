package org.system
package test

import akka.actor.Props
import akka.camel.CamelMessage
import akka.testkit.TestProbe
import org.system.core.actors.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}
import org.system.test.spec.SystemActorSpec

import scala.language.postfixOps

case class Message(test: String)

class CamelSystemTestSuite extends SystemActorSpec {

  "A Producer and consumer" must {
    pending

    val probe  = TestProbe()
    val consumer = system actorOf(Props(classOf[CommandConsumerSystemActor],probe ref, ??? /*TODO : need to provide properly */), "Consumer")
    val producer = system actorOf(Props(classOf[CommandProducerSystemActor],??? /*TODO : need to provide properly */), "Producer")

    "send and receive a simple message" in {
      producer ! Message("first")

      (probe expectMsgType)[CamelMessage]
    }

  }
}

package org
package concept
import akka.actor.{Actor, ActorSystem, Props}
import akka.camel._
import com.typesafe.config.ConfigFactory
import org.apache.activemq.ScheduledMessage._
import org.apache.activemq.camel.component.ActiveMQComponent

/**
 * Created by evgeniikorniichuk on 22/03/15.
 */
object CamelMinimalApp extends App {

  case class Message(body: String)

  class SimpleProducer() extends Actor with Producer with Oneway {
    def endpointUri: String = "activemq:foo.bar"
  }

  class SimpleConsumer() extends Actor with Consumer {
    def endpointUri: String = "activemq:foo.bar"

    def receive = {
      case msg: CamelMessage => println(msg)
    }
  }

  def instance = this

  implicit val config = ConfigFactory load()

  val actorSystem = ActorSystem("CamelTesting")
  val system = CamelExtension(actorSystem)

  val amqUrl = org.system.default("mqURL")
  (system context) addComponent("activemq", ActiveMQComponent activeMQComponent amqUrl)

  val simpleConsumer = actorSystem actorOf Props[SimpleConsumer]
  val simpleProducer = actorSystem actorOf Props[SimpleProducer]

  Thread sleep 100 // wait for setup

  simpleProducer ! Message("first")
  simpleProducer ! Message("second")
  simpleProducer ! Message("third")

  val delayedMessage = CamelMessage(Message("delayed fourth"), Map(AMQ_SCHEDULED_DELAY -> 3000))
  simpleProducer ! delayedMessage

  Thread sleep 5000 // wait for messages
  actorSystem shutdown()
}

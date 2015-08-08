package org.system.app.actors.main

import akka.actor._
import akka.camel.CamelExtension
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.activemq.camel.component.ActiveMQComponent
import org.system.app.command.jmx.{RootExecutorCompleted, StartRootExecutor}
import org.system.core.actors.System.SystemActor
import org.system.core.actors.main.RootExecutor
import org.system.core.actors.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}
import org.system.core.command.manage.StartSuite

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

// TODO JMX is not correct way of integration with end user:
// TODO in best implementation this should be just messanging queue listener
// TODO commands that it can retrieve should be explicitly declared in integration library for example
// TODO Refactoring is required
object JMXManager extends LazyLogging {

  def apply(jmxConfig: Config): JMXManager = {

    require(jmxConfig hasPath "mqComponent", "command mq component not found")
    require(jmxConfig hasPath "commandProducerUrl", "command producer url not found")
    require(jmxConfig hasPath "commandConsumerUrl", "command consumer url not found")

    new JMXManager(jmxConfig)
  }
}

class JMXManager private(jmxConfig: Config) extends SystemActor {

  // TODO Need to decide on messages interface for commands
  val producerRef = context actorOf(Props(CommandProducerSystemActor(jmxConfig)), "CommandProducer")
  val consumerRef = context actorOf(Props(CommandConsumerSystemActor(jmxConfig)), "CommandConsumer")

  val camel = CamelExtension(context system)

  (camel context) removeComponent (jmxConfig getString "mqComponent")
  (camel context) addComponent(jmxConfig getString "mqComponent", ActiveMQComponent activeMQComponent)

  val endpointF = (camel activationFutureFor consumerRef)(10 seconds, (context system) dispatcher)

  Await ready(endpointF, 10 seconds)

  def receive: Receive = jmxCommand(IndexedSeq())

  def jmxCommand(seq:IndexedSeq[ActorRef]): Receive = {
    case StartRootExecutor(rootExecutorDir) =>
      val ref = (context system) actorOf(Props(RootExecutor(rootExecutorDir, hasJMXExecutor = true)), rootExecutorDir name)

      context become jmxCommand(seq :+ ref)

      ref ! StartSuite
    case RootExecutorCompleted =>
      context become jmxCommand(seq filterNot (ref => (ref path) equals (sender() path)))
  }

}
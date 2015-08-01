package org.system
package core
package actors
package main

import akka.actor._
import akka.camel.CamelExtension
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.apache.activemq.camel.component.ActiveMQComponent
import org.system.core.actors.System.SystemActor
import org.system.core.actors.queue.{CommandConsumerSystemActor, CommandProducerSystemActor}
import org.system.core.command.jmx.{RootExecutorCompleted, StartRootExecutor}
import org.system.core.command.manage.StartSuite

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object JMXManager extends LazyLogging {

  def apply(jmxConfig: Config): JMXManager = {

    require(jmxConfig hasPath "mqComponent", "command mq component not found")
    require(jmxConfig hasPath "commandProducerUrl", "command producer url not found")
    require(jmxConfig hasPath "commandConsumerUrl", "command consumer url not found")

    new JMXManager(jmxConfig)
  }
}

class JMXManager private(jmxConfig: Config) extends SystemActor {

  import context.system

  // TODO Need to decide on messages interface for commands
  val producerRef = context actorOf(Props(CommandProducerSystemActor(jmxConfig)), "CommandProducer")
  val consumerRef = context actorOf(Props(CommandConsumerSystemActor(jmxConfig)), "CommandConsumer")

  val camel = CamelExtension(system)

  (camel context) removeComponent (jmxConfig getString "mqComponent")
  (camel context) addComponent(jmxConfig getString "mqComponent", ActiveMQComponent activeMQComponent)

  val endpointF = (camel activationFutureFor consumerRef)(10 seconds, system dispatcher)

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
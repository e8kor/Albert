package org.system.app.actors

import javax.management.{JMException, JMRuntimeException}

import akka.actor._
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging
import org.system.app.command.jmx.{RootExecutorCompleted, StartRootExecutor}
import org.system.app.jmx.MXBeanActor
import org.system.core.actors.System.SystemActor
import org.system.core.actors.main.RootExecutor
import org.system.core.command.manage.StartSuite

import scala.language.postfixOps

// TODO JMX is not correct way of integration with end user:
// TODO in best implementation this should be just messanging queue listener
// TODO commands that it can retrieve should be explicitly declared in integration library for example
// TODO Refactoring is required
object ApplicationManager extends LazyLogging {

  def apply(jmxConfig: Config): ApplicationManager = {

    require(jmxConfig hasPath "mqComponent", "command mq component not found")
    require(jmxConfig hasPath "commandProducerUrl", "command producer url not found")
    require(jmxConfig hasPath "commandConsumerUrl", "command consumer url not found")

    new ApplicationManager(jmxConfig)
  }
}

class ApplicationManager private(jmxConfig: Config) extends SystemActor {

  import akka.actor.OneForOneStrategy
  import akka.actor.SupervisorStrategy._

  import scala.concurrent.duration._

  override val supervisorStrategy = {
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case e: JMRuntimeException =>
        log error(e, "Supervisor strategy STOPPING actor from errors during JMX invocation")
        Stop
      case e: JMException =>
        log error(e, "Supervisor strategy STOPPING actor from incorrect invocation of JMX registration")
        Stop
      case t =>
        // Use the default supervisor strategy otherwise.
        log error(t, "Supervisor strategy ESCALATE actor from incorrect invocation of JMX registration")
        (super.supervisorStrategy decider) applyOrElse(t, (_: Any) => Escalate)
    }
  }

  //  TODO Need to decide on messages interface for commands
  //  val producerRef = context actorOf(Props(CommandProducerSystemActor(jmxConfig)), "CommandProducer")
  //  val consumerRef = context actorOf(Props(CommandConsumerSystemActor(jmxConfig)), "CommandConsumer")
  //  val camel = CamelExtension(context system)
  //  (camel context) removeComponent (jmxConfig getString "mqComponent")
  //  (camel context) addComponent (jmxConfig getString "mqComponent", ActiveMQComponent activeMQComponent)
  //  val endpointF = (camel activationFutureFor consumerRef)(10 seconds, (context system) dispatcher)
  //  Await ready(endpointF, 10 seconds)

  val mxBean = context actorOf(Props(MXBeanActor()), "mxBean")

  def receive: Receive = jmxCommand(IndexedSeq())

  def jmxCommand(seq: IndexedSeq[ActorRef]): Receive = {
    case StartRootExecutor(rootExecutorDir) =>
      val ref = (context system) actorOf(Props(RootExecutor(rootExecutorDir)), rootExecutorDir name)

      context watch ref
      context become jmxCommand(seq :+ ref)

      ref ! StartSuite
    case RootExecutorCompleted =>
      context become jmxCommand(seq filterNot (ref => (ref path) equals (sender() path)))
    case term: Terminated =>
      val (left, right) = seq partition (ref => (ref path) equals ((term actor) path))
      if (left isEmpty) {
        log info s"Got Termination command but no root executor founded, path ${(term actor) path}"
      } else {
        left foreach {
          ref =>
            log info s"RootExecutor by path: ${ref path} completed his work"
        }
      }
      context become jmxCommand(right)
  }

}
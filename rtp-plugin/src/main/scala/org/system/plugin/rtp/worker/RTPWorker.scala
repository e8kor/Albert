package org.system
package plugin.rtp.worker

import akka.actor._
import org.system.plugin.rtp.mq.{RTPConsumerActor, RTPProducerActor}
import org.system.plugin.rtp.scenario.RTPScenario
import org.system.plugin.worker.WorkerActorPlugin

import scala.language.postfixOps

/**
 * Created by nutscracker on 6/30/2014.
 */
class RTPWorker() extends WorkerActorPlugin {

  type PluginScenario = RTPScenario

  context watch (context parent)

  val consumer = (context system) actorOf(Props(classOf[RTPConsumerActor]), "RTPConsumer")
  val producer = (context system) actorOf(Props(classOf[RTPProducerActor]), "RTPProducer")

  override def receive: Receive = normal

  private def normal: Receive = {
    case Terminated(actorRef) ⇒
      log info freeText("terminatingWorker")
      self ! PoisonPill
    case eSuite:PluginScenario =>
      implicit val eConfig = eSuite // TODO ability to parse scenario

  }


}

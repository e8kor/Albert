package org.system
package plugin.rtp.worker

import akka.actor._
import com.typesafe.config.Config
import org.system.command.manage.WrongSuiteExecutor
import org.system.plugin.rtp.mq.{RTPProducerActor, RTPConsumerActor}
import org.system.plugin.worker.WorkerActorPlugin
import org.system.scenario.Scenario

import scala.language.postfixOps
import scala.util.Try

/**
 * Created by nutscracker on 6/30/2014.
 */
class RTPWorker() extends WorkerActorPlugin {

  context watch (context parent)

  val consumer = (context system) actorOf(Props(classOf[RTPConsumerActor]), "RTPConsumer")
  val producer = (context system) actorOf(Props(classOf[RTPProducerActor]), "RTPProducer")

  override def receive: Receive = normal

  private def normal: Receive = {
    case Terminated(actorRef) ⇒
      log info freeText("terminatingWorker")
      self ! PoisonPill
    case eSuite:Scenario =>
      implicit val eConfig = (eSuite) // TODO ability to parse scenario

  }

  private def processAction(implicit config:Config) = {
    (Try {
      Class forName lookForValue[String]("executionActor")
    } map {
      clazz =>
        (context system) actorOf Props(clazz)
    } toOption) toRight (context parent) match {
      case Left(actor) =>
        actor ! WrongSuiteExecutor
      case Right(actor) =>
        actor ! config
        ??? // TODO Need to implement and make some diagram of work from
    }
  }

}

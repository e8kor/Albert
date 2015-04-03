package org.system
package plugin.rtp.reader

import akka.actor.{PoisonPill, Terminated}
import com.typesafe.config.{Config, ConfigFactory}
import org.implicits._
import org.system.plugin.model.scenario.Scenario
import org.system.plugin.reader.ReaderActorPlugin

import scala.language.postfixOps
import scala.reflect.io.Directory

/**
 * Created by nutscracker on 7/22/2014.
 */
class RTPConfigReader(dir: Directory, rootConfig:Config) extends ReaderActorPlugin {

  context watch (context parent)

  val config = (dir suiteConfig()) map ( file => ConfigFactory parseFile (file jfile) withFallback rootConfig)

  override def receive = normal

  private def normal: Receive = {
    case preparedConfig:Scenario ⇒
      (context parent) ! preparedConfig
    case Terminated(actorRef) ⇒
      log info freeText("terminatingConfigReader")
      self ! PoisonPill
  }

}

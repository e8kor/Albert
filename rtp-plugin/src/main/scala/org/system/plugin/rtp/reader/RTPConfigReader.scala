package org.system
package plugin.rtp.reader

import com.typesafe.config.Config
import org.system.plugin.reader.ReaderActorPlugin
import org.system.scenario.Scenario

import scala.language.postfixOps
import scala.reflect.io.Directory

/**
 * Created by nutscracker on 7/22/2014.
 */
class RTPConfigReader(dir: Directory, suiteConfig:Config) extends ReaderActorPlugin {

// TODO expect message to reread configuration
  override def receive = normal

  private def normal: Receive = {
    case preparedConfig:Scenario ⇒
      (context parent) ! preparedConfig
  }

}

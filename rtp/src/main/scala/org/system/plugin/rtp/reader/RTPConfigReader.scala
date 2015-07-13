package org.system
package plugin.rtp.reader

import com.typesafe.config.Config
import org.system.plugin.reader.ReaderActorPlugin
import org.system.plugin.rtp.scenario.RTPScenario

import scala.language.postfixOps
import scala.reflect.io.Directory

class RTPConfigReader(dir: Directory, suiteConfig:Config) extends ReaderActorPlugin {

  override type PluginScenario = RTPScenario
// TODO expect message to reread configuration
  override def receive = normal

  private def normal: Receive = {
    case preparedConfig:PluginScenario ⇒
      (context parent) ! preparedConfig
  }

}

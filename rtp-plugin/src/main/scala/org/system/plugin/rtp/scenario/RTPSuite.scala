package org.system.plugin.rtp.scenario

import com.typesafe.config.Config
import org.system.{FileId, TransportConfigId, TransportId}
import org.system.scenario.{TransportConfiguration, ScenarioTransport, Suite}

import scala.reflect.io.File

/**
 * Created by evgeniikorniichuk on 19/04/15.
 */
class RTPSuite() extends Suite[RTPSuite]{

  override type SuiteScenario = RTPScenario

  override def config: Config = ???

  override def scenario: SuiteScenario = ???

  override def data: Map[FileId, File] = ???

  override def transportConfigurations: Map[TransportConfigId, TransportConfiguration] = ???

  override def transport: Map[TransportId, ScenarioTransport] = ???

}

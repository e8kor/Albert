package org.system.plugin.rtp.scenario

import org.system.scenario.{ScenarioTransport, Suite, TransportConfiguration}
import org.system.types.{FileId, TransportConfigId, TransportId}

import scala.reflect.io.File

/**
 * Created by evgeniikorniichuk on 19/04/15.
 */
case class RTPSuite(
                     config: DBConfig,
                     scenario: RTPScenario,
                     messages: Map[FileId, File],
                     transportConfigurations: Map[TransportConfigId, TransportConfiguration],
                     transport: Map[TransportId, ScenarioTransport]
                     ) extends Suite[RTPSuite] {

  override type SuiteScenario = RTPScenario

}

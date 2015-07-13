package org.system.plugin.rtp.scenario

import org.system.scenario.Scenario

/**
 * Created by evgeniikorniichuk on 19/04/15.
 */
case class RTPScenario(
                   scenarioSteps: IndexedSeq[RTPStep]
                   ) extends Scenario[RTPScenario] {

  override type Step = RTPStep

}

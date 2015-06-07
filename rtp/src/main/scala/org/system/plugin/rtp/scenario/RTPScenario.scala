package org.system.plugin.rtp.scenario

import org.system.scenario.Scenario

/**
 * Created by evgeniikorniichuk on 19/04/15.
 */
class RTPScenario extends Scenario[RTPScenario]{

  override type Step = RTPStep

  override def scenarioSteps: Seq[Step] = ???
}

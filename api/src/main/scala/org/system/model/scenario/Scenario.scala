package org.system.model.scenario

import org.system.ID

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait Scenario {

  def scenarioSteps:Seq[ScenarioStep]

  def getStep(id:ID) = scenarioSteps find( step =>  (step id) equals id)

}

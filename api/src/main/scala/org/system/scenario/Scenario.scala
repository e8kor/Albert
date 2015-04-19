package org.system.scenario

import org.system.StepId

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait Scenario[T <: Scenario] {

  self:T =>

  def scenarioSteps:Seq[ScenarioStep]

  def getStep(id:StepId) = scenarioSteps find( step =>  (step id) equals id)

}

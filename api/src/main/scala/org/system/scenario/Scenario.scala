package org.system.scenario

import org.system.types.StepId

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait Scenario[T <: Scenario[T]] {

  self:T =>

  type Step <: ScenarioStep[Step]

  def scenarioSteps:Seq[Step]

  def getStep(id:StepId) = scenarioSteps find( step =>  (step id) equals id)

}

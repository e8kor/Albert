package org.system.scenario

import org.system.types.StepId

import scala.language.postfixOps

trait Scenario[T <: Scenario[T]] {

  self:T =>

  type Step <: ScenarioStep[Step]

  def scenarioSteps:Seq[Step]

  def getStep(id:StepId) = scenarioSteps find( step =>  (step id) equals id)

}

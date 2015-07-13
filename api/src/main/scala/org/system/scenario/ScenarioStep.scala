package org.system.scenario

import org.system.types.{Description, StepId}

trait ScenarioStep[T <: ScenarioStep[T]] {
  self:T =>

  type Action <: ScenarioAction[Action]

  def id:StepId

  def description:Description

  def action:Action

}

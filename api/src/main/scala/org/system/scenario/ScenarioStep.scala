package org.system.scenario

import org.system.{Description, StepId}

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait ScenarioStep[T <: ScenarioStep[T]] {
  self:T =>

  type Action <: ScenarioAction[Action]

  def id:StepId

  def description:Description

  def action:Action

}

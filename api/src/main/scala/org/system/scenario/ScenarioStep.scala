package org.system.scenario

import org.system.{ActionType, Description, StepId}

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait ScenarioStep[T <: ScenarioStep] {
  self:T =>

  def id:StepId

  def tpe:ActionType

  def description:Description

  def action:ScenarioAction

}

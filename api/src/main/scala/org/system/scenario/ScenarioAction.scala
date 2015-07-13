package org.system.scenario

import org.system.types.ActionType

import scala.concurrent.Future

trait ScenarioAction[T <: ScenarioAction[T]] {
  self:T =>

  def tpe:ActionType

  def execute():Future[Unit]
}


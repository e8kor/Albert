package org.system.scenario

import org.system.ActionType

import scala.concurrent.Future

/**
 * Created by evgeniikorniichuk on 18/04/15.
 */
trait ScenarioAction[T <: ScenarioAction[T]] {
  self:T =>

  def tpe:ActionType

  def execute():Future[Unit]
}


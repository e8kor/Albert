package org.system.scenario

import scala.concurrent.Future

/**
 * Created by evgeniikorniichuk on 18/04/15.
 */
trait ScenarioAction[T <: ScenarioAction[T]] {
  self:T =>

  def execute():Future[Unit]
}


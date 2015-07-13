package org.system.plugin.rtp.scenario

import org.system.types.ActionType
import org.system.scenario.ScenarioAction

import scala.concurrent.Future

/**
 * Created by evgeniikorniichuk on 19/04/15.
 */
class RTPAction[A] extends ScenarioAction[RTPAction[A]]{

  override def tpe: ActionType = ???

  override def execute(): Future[A] = ???

}

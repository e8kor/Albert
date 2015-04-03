package org.system.plugin.model.scenario

import org.system.{Description, ID, Type}

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait ScenarioStep {

  def id:ID

  def tpe:Type

  def description:Description

}

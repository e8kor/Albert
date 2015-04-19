package org.system.scenario

import org.system.{TransportId, Description, TransportType}

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait ScenarioTransport[T <: ScenarioTransport] {
  self:T =>

  def apply(configuration: TransportConfiguration):T

  def id: TransportId

  def tpe: TransportType

  def clazz: Class[_]

  def description: Description

}

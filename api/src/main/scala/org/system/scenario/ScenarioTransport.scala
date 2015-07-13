package org.system.scenario

import org.system.types.{TransportId, Description, TransportType}

trait ScenarioTransport {

  def id: TransportId

  def tpe: TransportType

  def clazz: Class[_]

  def description: Description

}

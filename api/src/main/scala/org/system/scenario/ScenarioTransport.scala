package org.system.scenario

import org.system.types.{TransportId, Description, TransportType}

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait ScenarioTransport {

  def id: TransportId

  def tpe: TransportType

  def clazz: Class[_]

  def description: Description

}

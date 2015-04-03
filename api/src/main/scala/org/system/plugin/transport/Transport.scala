package org.system.plugin.transport

import org.system.plugin.model.config.TransportConfig
import org.system.{Description, ID, Type}

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait Transport {

  def id: ID

  def tpe: Type

  def clazz: Class[_]

  def description: Description

  def connectionConfig: TransportConfig

}

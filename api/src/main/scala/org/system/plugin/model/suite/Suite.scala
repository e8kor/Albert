package org.system.plugin.model.suite

import com.typesafe.config.Config
import org.system.{FileName, ID}
import org.system.plugin.model.scenario.Scenario
import org.system.plugin.transport.Transport

import scala.reflect.io.File

/**
 * Created by evgeniikorniichuk on 22/03/15.
 */
trait Suite {

  def config:Config

  def transport:Map[ID,Transport]

  def scenario:Scenario

  def data:Map[FileName, File]

}

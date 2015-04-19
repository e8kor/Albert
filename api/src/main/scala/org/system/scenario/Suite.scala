package org.system.scenario

import com.typesafe.config.Config
import org.system.{FileId, TransportConfigId, TransportId}

import scala.reflect.io.File

/**
 * Created by evgeniikorniichuk on 22/03/15.
 */
trait Suite {

  def config:Config

  def transport:Map[TransportId,ScenarioTransport]

  def transportConfigurations:Map[TransportConfigId,TransportConfiguration]

  def scenario:Scenario

  def data:Map[FileId, File]

}

package org.system.scenario

import com.typesafe.config.Config
import org.system.types.{FileId, TransportConfigId, TransportId}

import scala.reflect.io.File

/**
 * Created by evgeniikorniichuk on 22/03/15.
 */
trait Suite[SelfType <: Suite[SelfType]] {
  self:SelfType=>
  
  type SuiteScenario <: Scenario[SuiteScenario]

  def config:Config

  def transport:Map[TransportId,ScenarioTransport]

  def transportConfigurations:Map[TransportConfigId,TransportConfiguration]

  def scenario:SuiteScenario

  def data:Map[FileId, File]

}

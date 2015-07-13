package org.system.command.manage

import com.typesafe.config.Config
import org.system.command.Responce

import scala.reflect.io.Directory

case class StartWork(suiteDir:Directory, suiteConfig:Config) extends Responce


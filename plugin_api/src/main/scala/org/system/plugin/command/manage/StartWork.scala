package org.system.plugin.command.manage

import com.typesafe.config.Config
import org.system.api.command.Request

import scala.reflect.io.Directory

case class StartWork(suiteDir: Directory, suiteConfig: Config) extends Request


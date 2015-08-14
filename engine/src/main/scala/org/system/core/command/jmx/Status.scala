package org.system.core.command.jmx

import org.system.api.command.Request

import scala.reflect.io.Directory

sealed trait JMX extends Request

case class StartRootExecutor(rootExecutorDir: Directory) extends JMX

case object RootExecutorCompleted
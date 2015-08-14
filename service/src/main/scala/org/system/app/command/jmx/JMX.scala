package org.system.app.command.jmx

import org.system.api.command.{Response, Request}

import scala.reflect.io.Directory

sealed trait JMX

case object RootExecutorCompleted extends JMX with Response

case class StartRootExecutor(rootExecutorDir: Directory) extends JMX with Request
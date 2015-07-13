package org.system.command.manage

import org.system.command.Request
import akka.actor.PossiblyHarmful

import scala.reflect.io.{Directory, Path}

case class PrepareConfig(path: Directory) extends Request


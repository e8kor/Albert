package org.system.command.manage

import org.system.command.Request
import akka.actor.PossiblyHarmful

import scala.reflect.io.{Directory, Path}

case class Restart(path: Path) extends Request with PossiblyHarmful


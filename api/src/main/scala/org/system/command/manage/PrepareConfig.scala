package org.system.command.manage

import org.system.command.Request
import akka.actor.PossiblyHarmful

import scala.reflect.io.{Directory, Path}

/**
 * Created by evgeniikorniichuk on 06/06/15.
 */
case class PrepareConfig(path: Directory) extends Request


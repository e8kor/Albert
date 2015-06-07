package org.system.command.manage

import akka.actor.PossiblyHarmful
import org.system.command.Responce

import scala.reflect.io.{Directory, Path}

/**
 * Created by evgeniikorniichuk on 06/06/15.
 */
case class WrongSuitePath(path: Path) extends Responce


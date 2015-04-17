package org.system.command

import org.system.model.command.{Responce, Request}

/**
 * Created by evgeniikorniichuk on 24/03/15.
 */
package object manage {

  import akka.actor.PossiblyHarmful

  import scala.reflect.io.{Directory, Path}

  case object WhatIsYourStatus extends Request

  case class PrepareConfig(path: Directory) extends Request

  case class Restart(path: Path) extends Request with PossiblyHarmful

  case class WrongSuitePath(path: Path) extends Responce
  
  case object Stop extends Request with PossiblyHarmful

  case object WrongSuiteExecutor extends Responce

  case object WorkCompleted extends Responce

  case object SuiteCompleted extends Responce

}

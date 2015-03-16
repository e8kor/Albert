package org.system

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object command {

  import akka.actor.PossiblyHarmful
  import scala.reflect.io.Path

  sealed trait Command

  case class WrongSuitePath(path: Path) extends Command

  case class Start(path: Path) extends Command

  case class Watch(path: Path) extends Command

  case class Restart(path: Path) extends Command with PossiblyHarmful

  case object Stop extends Command with PossiblyHarmful

  case object WorkCompleted extends Command

  case object SuiteCompleted extends Command

}
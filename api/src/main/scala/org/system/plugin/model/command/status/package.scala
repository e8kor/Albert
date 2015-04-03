package org.system.plugin.model.command

/**
 * Created by evgeniikorniichuk on 24/03/15.
 */
package object status {

  sealed trait Status extends Responce

  case object ImWorking extends Status

  case object ImRestarting extends Status

  case object TryingToStopMySelf extends Status

  case object ReadingConfig extends Status

  case object InitiatingSequence extends Status

  case object WaitingForSlaves extends Status

  case object WaitingForPath extends Status

}

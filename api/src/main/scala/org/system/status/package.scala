package org.system

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object statuses {

  sealed trait Status

  case object WhatIsYourStatus extends Status

  case object ImWorking extends Status

  case object ImRestarting extends Status

  case object TryingToStopMySelf extends Status

  case object ReadingConfig extends Status

  case object InitiatingSequence extends Status

  case object WaitingForSlaves extends Status

  case object WaitingForPath extends Status

}

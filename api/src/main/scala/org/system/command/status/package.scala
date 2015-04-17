package org.system.command

import org.system.model.command.Responce

/**
 * Created by evgeniikorniichuk on 24/03/15.
 */
package object status {

  sealed trait Status extends Responce

  case object Working extends Status

  case object Restarting extends Status
  
  case object ReadingConfig extends Status

  case object WaitingForSubSuite extends Status

}

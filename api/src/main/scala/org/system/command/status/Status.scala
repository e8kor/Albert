package org.system.command.status

import org.system.command.Responce

/**
 * Created by evgeniikorniichuk on 06/06/15.
 */
sealed trait Status extends Responce

case object Working extends Status

case object Restarting extends Status

case object ReadingConfig extends Status

case object WaitingForSubSuite extends Status
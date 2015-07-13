package org.system.command.status

import org.system.command.Responce

sealed trait Status extends Responce

case object Working extends Status

case object Restarting extends Status

case object ReadingConfig extends Status

case object WaitingForSubSuite extends Status
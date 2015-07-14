package org.system.core.command.status

import org.system.api.command.Responce

sealed trait Status extends Responce

case object Working extends Status

case object ReadingConfig extends Status

case object WaitingForSubSuite extends Status
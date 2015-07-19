package org.system.core.command.status

import org.system.api.command.Response

sealed trait Status extends Response

case object Working extends Status

case object ReadingConfig extends Status

case object WaitingForSubSuite extends Status
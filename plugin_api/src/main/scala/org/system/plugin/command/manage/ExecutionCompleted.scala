package org.system.plugin.command.manage

import org.system.api.command.Response

sealed trait ExecutionCompleted extends Response

case object ExecutionSuccessfullyCompleted extends ExecutionCompleted

case object ExecutionFailed extends ExecutionCompleted


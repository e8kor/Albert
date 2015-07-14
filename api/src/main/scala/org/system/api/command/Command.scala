package org.system.api.command

sealed trait Command

trait Request extends Command

trait Responce extends Command
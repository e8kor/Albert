package org.system.core.command.manage

import org.system.api.command.Request

import akka.actor.PossiblyHarmful

case object Stop extends Request with PossiblyHarmful


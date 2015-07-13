package org.system.command.manage

import org.system.command.Request

import akka.actor.PossiblyHarmful

case object Stop extends Request with PossiblyHarmful


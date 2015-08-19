package org.system.core.command.manage

import akka.actor.PossiblyHarmful
import org.system.api.command.Request

case object Stop extends Request with PossiblyHarmful


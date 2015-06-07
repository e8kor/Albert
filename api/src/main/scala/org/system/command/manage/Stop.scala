package org.system.command.manage

import org.system.command.Request

import akka.actor.PossiblyHarmful
/**
 * Created by evgeniikorniichuk on 06/06/15.
 */
case object Stop extends Request with PossiblyHarmful


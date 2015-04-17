package org.utils

import akka.actor.Actor._

/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
class ReceiveOps(val receive: Receive) extends AnyVal

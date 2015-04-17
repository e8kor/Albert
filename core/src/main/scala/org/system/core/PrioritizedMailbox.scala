package org.system
package core

import akka.actor.ActorSystem
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config

/**
 * Created by nutscracker on 10/5/2014.
 */
/*
 * TODO Prioritized mailbox must filter commands cross application
 */
class PrioritizedMailbox(settings: ActorSystem.Settings, cfg: Config) extends UnboundedPriorityMailbox(
  PriorityGenerator {
    case _ ⇒ 1
  })

package org.system.actor

import akka.actor.ActorSystem
import akka.dispatch.{PriorityGenerator, UnboundedPriorityMailbox}
import com.typesafe.config.Config
import org.system.message.{Status, Command}

/**
 * Created by nutscracker on 10/5/2014.
 */
class PrioritizedMailbox(settings: ActorSystem.Settings, cfg: Config) extends UnboundedPriorityMailbox(
  PriorityGenerator {
    case any:Status ⇒ 1
    case any:Command ⇒ 5
    case _ ⇒ 10
  })

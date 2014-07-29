package com.rtp.dev.actor

import akka.actor.Actor.Receive
import akka.actor.Props
import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.MonitorActor
import com.rtp.dev.{Restart, Start, SuitePath}

/**
 * Created by nutscracker on 6/30/2014.
 */
object Runner {

  def apply() = Props(classOf[Runner])

}

class Runner extends RTActor {

  override def receive: Receive = {

    case task:Task => ???

  }

}

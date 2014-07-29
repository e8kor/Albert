package org.albert.actor

import akka.actor.Props

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

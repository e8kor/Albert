package org.system.core.actors.track

import org.system.core.actors.System.SystemActor
import org.system.core.command.track.Tracking

import scala.language.postfixOps

object EventTracker {

  def apply() = {
    new EventTracker()
  }

}

class EventTracker private() extends SystemActor {

  ((context system) eventStream) subscribe(self, classOf[Tracking])

  override def receive: Receive = {
    case request: Tracking =>
      log info
        s"""EventMonitor :
            |got request event: $request
            |sender: ${sender() path}
         """.stripMargin

    case other =>
      log info
        s"""EventMonitor :
            |got non recognized event: $other
            |sender: ${sender() path}
         """.stripMargin
  }

}

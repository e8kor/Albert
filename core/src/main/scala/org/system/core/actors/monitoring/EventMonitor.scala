package org.system.core.actors.monitoring

import org.system.api.command.{Request, Response}
import org.system.core.actors.System.SystemActor

class EventMonitor extends SystemActor {

  override def receive: Receive = {
    case request: Request =>
      log info
        s"""EventMonitor :
            |got request event: $request
            |sender: ${sender() path}
         """.stripMargin
    case response: Response =>
      log info
        s"""EventMonitor :
            |got response event: $response
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

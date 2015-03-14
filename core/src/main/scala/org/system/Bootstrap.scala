package org.system

import org.system.actor.{RootExecutor, Terminator, withProps}
import org.system.implicits.PathOps

import scala.language.postfixOps
import scala.reflect.io.Path

class Bootstrap extends akka.kernel.Bootable {

  override def startup() {
    prepareCamel(actorSystem)
    val rootRef = actorSystem actorOf(
      withProps[RootExecutor](Path(default("rootDir")) dirOrParentDir()),
      default("rootActor")
      )
    actorSystem actorOf(
      withProps[Terminator](rootRef),
      default("terminatorActor")
      )
  }

  override def shutdown() {
    actorSystem shutdown()
    sys exit 0
  }

}
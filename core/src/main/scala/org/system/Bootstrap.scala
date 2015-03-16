package org.system

import akka.actor.ActorSystem
import org.system.actor.{Terminator, RootExecutor}
import org.system.api.actor.withProps
import org.implicits.PathOps

import scala.language.postfixOps
import scala.reflect.io.Path

class Bootstrap extends akka.kernel.Bootable {

  val actorSystem = ActorSystem create default("systemName")

  override def startup() {
    ActorSystem create()
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
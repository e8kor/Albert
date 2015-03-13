package org.system

import org.system.actor.{RootExecutor, Terminator, withProps}
import org.system.command.Start

import scala.reflect.io.Path
import scala.sys.props

class Bootstrap extends akka.kernel.Bootable {

  override def startup() {
    val rootDir = default("rootDir")
    val rootRef = actorSystem actorOf(withProps[RootExecutor](), default("rootActor"))
    actorSystem actorOf(withProps[Terminator](rootRef), default("terminatorActor"))
    rootRef ! Start(Path(props getOrElse(rootDir, sys env rootDir)))
  }

  override def shutdown() {
    actorSystem shutdown()
    sys exit 0
  }

}
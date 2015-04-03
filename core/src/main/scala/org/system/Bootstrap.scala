package org.system

import akka.actor.{ActorSystem, Props}
import org.implicits.PathOps
import org.system.core.delegat.Terminator
import org.system.core.main.RootExecutor

import scala.language.postfixOps
import scala.reflect.io.Path

class Bootstrap extends akka.kernel.Bootable {

  val actorSystem = ActorSystem create default("systemName")

  override def startup() {
    prepareCamel(actorSystem)
    val rootRef = actorSystem actorOf(Props(classOf[RootExecutor], Path(default("rootDir")) dirOrParentDir()), default("rootActor"))
    actorSystem actorOf(Props(classOf[Terminator], rootRef), default("terminatorActor"))
  }

  override def shutdown() {
    actorSystem shutdown()
    sys exit 0
  }

}
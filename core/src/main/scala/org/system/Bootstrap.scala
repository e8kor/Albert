package org.system

import akka.actor.ActorSystem
import akka.kernel.Bootable
import org.system.core.delegat.Terminator
import org.system.core.main.RootExecutor

import scala.language.postfixOps

trait BootstrapComponent extends Bootable {

  import akka.actor.{ActorSystem, Props}
  import org.implicits.path2PathOps

  import scala.reflect.io.Path

  def actorSystem: ActorSystem

  override def startup() {
    prepareCamel(actorSystem)

    val rootRef = actorSystem actorOf(
      Props(classOf[RootExecutor], Path(default("rootDir")) dirOrParentDir()),
      default("rootActor"))

    actorSystem actorOf(
      Props(classOf[Terminator], rootRef),
      default("terminatorActor"))
  }

  override def shutdown() {
    actorSystem shutdown()
    sys exit 0
  }

}

object Bootstrap extends BootstrapComponent {

  override val actorSystem = ActorSystem create default("systemName")

}
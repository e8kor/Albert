package org.system

import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import org.system.core.actors.delegat.Terminator
import org.system.core.actors.main.RootExecutor

import scala.language.postfixOps

trait BootstrapComponent extends Bootable {

  import akka.actor.{ActorSystem, Props}
  import org.implicits.path2PathOps

  import scala.reflect.io.Path

  def actorSystem: ActorSystem

  def config: Config

  override def startup(): Unit = {

    val rootRef = actorSystem actorOf(
      Props[RootExecutor](  RootExecutor(ConfigFactory load "root.conf")),
      "RootExecutor")

    val termRef = actorSystem actorOf(
      Props[Terminator]( new Terminator(rootRef)(config)),
      "RootExecutorWatcher")
  }

  override def shutdown(): Unit = {
    actorSystem shutdown()
    sys exit 0
  }

}

object Bootstrap extends BootstrapComponent {

  override val actorSystem = ActorSystem create "Albert"

  override implicit def config: Config = ConfigFactory load()
}
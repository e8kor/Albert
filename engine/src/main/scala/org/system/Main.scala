package org.system

import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration._
import com.typesafe.config.{Config, ConfigFactory}
import org.system.core.actors.delegat.Terminator
import org.system.core.actors.main.RootExecutor

import scala.concurrent.Await
import scala.language.postfixOps
import scala.reflect.io.Directory

trait BootstrapComponent {
  self:App =>

  def actorSystem: ActorSystem

  def config: Config


  def startup(): Unit = {

    require((sys props) contains "albertRoot", "invalid startup: directory under 'albertRoot' not defined")

    val startUpDir = Directory((sys props) ("albertRoot"))

    require(startUpDir exists, s"invalid startup: directory not exists by \n path: ${startUpDir path}")

    val rootRef = actorSystem actorOf(
      Props[RootExecutor](RootExecutor(startUpDir)),
      "RootExecutor")

    val termRef = actorSystem actorOf(
      Props[Terminator]( new Terminator(rootRef)(config)),
      "RootExecutorWatcher")
  }

  def shutdown(): Unit = {
    Await result(actorSystem terminate(), 30 seconds)
    sys exit 0
  }

}

object Main extends BootstrapComponent with App {

  override val actorSystem = ActorSystem create "Albert"

  override val config: Config = ConfigFactory load()

  startup()
}
package org.system

import akka.actor.ActorSystem
import akka.kernel.Bootable
import com.typesafe.config.{Config, ConfigFactory}
import org.system.core.actors.delegat.Terminator
import org.system.core.actors.main.RootExecutor

import scala.language.postfixOps
import scala.reflect.io.Directory

trait BootstrapComponent extends Bootable {

  import akka.actor.{ActorSystem, Props}

  def actorSystem: ActorSystem

  def config: Config

  override def startup(): Unit = {

    // TODO how to pass root directory here ?
    // TODO first option via flag  -DalbertRoot=/some/path/to/suites
    // TODO second option via JMX command :
    // TODO jmx approach means that root.conf can be located separately from test suites
    // TODO additional root.conf fields need to be declared (or removed for example absence of root_directory)
    // TODO this approach need to be implemented

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

  override def shutdown(): Unit = {
    actorSystem shutdown()
    sys exit 0
  }

}

object Bootstrap extends BootstrapComponent {

  override val actorSystem = ActorSystem create "Albert"

  override implicit def config: Config = ConfigFactory load()
}
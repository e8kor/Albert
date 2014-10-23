package org.albert
package main

import scala.reflect.io.Path

class Bootstrap extends akka.kernel.Bootable {

  lazy val system = akka.actor.ActorSystem create "albert"

  override def startup(): Unit = {
    import scala.sys.props
    import org.albert.actor.{RootExecutor, Terminator}
    val app = system actorOf(RootExecutor(), "alb_root")
    system actorOf(Terminator(app), "alb_terminator")
    app ! Path(props getOrElse( albert_root, """C:\AlbertTestHome\"""))
  }

  override def shutdown(): Unit = {
    system shutdown()
    sys exit 0
  }
}
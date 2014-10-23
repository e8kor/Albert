package org.albert
package actor

import akka.actor.{ActorRef, OneForOneStrategy, SupervisorStrategy}
import com.beachape.filemanagement.MonitorActor
import org.albert.command._

import scala.reflect.io.Path

/**
 * Created by nutscracker on 6/30/2014.
 */
object RootExecutor extends ActorObject

class RootExecutor extends AlbertActor {

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case thr:Throwable =>
      import SupervisorStrategy.restart
      log error(thr, "trying to restart after {}", thr)
      restart
  }

  override def receive = normal

  def normal:Receive = {
    case Start(dir) =>
      self ! Watch(dir)
    case Watch(dir) =>
      val execute = validate[Path] _ andThen startSuiteManager
      execute apply dir
    case Restart(dir) =>
      ((context system) actorSelection (dir path)) ! Stop()
    case Stopped(dir) => self ! Watch(dir)
  }


  def startSuiteManager(either:Either[Path,Exception]) = {
    either match {
      case Right(exception) =>
        log error(exception, s"got illegal path")
        None
      case Left(valid) =>
        import valid.path
        val actor = (context system) actorOf (SuiteManager(), path)
        actor ! SuitePath(valid)
        Option(actor)
    }
  }
}
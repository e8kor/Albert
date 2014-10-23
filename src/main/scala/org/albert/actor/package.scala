package org.albert

import akka.actor.{LoggingFSM, FSM}

/**
 * Created by nutscracker on 6/30/2014.
 */

package object message {

  sealed trait Message

  trait Command extends Message

  trait Status extends Message

  trait State  extends Message

  trait SuiteStep extends Message

}

package object command {

  import akka.actor.PossiblyHarmful
  import org.albert.message.Command
  import scala.reflect.io.Path

  case class SuitePath(path:Path) extends Command

  case class WrongPath(path:Path) extends Command

  case class ParsedConfig(path:Path) extends Command

  case class InvalidSuitePath(path:Path) extends Command

  case class SubSuitePath(path:Path) extends Command

  case class Ready(path:Path) extends Command

  case class Start(path:Path) extends Command

  case class Watch(path:Path) extends Command

  case class Restart(path:Path) extends Command

  case class Stop() extends Command with PossiblyHarmful

  case class Stopped(path:Path) extends Command

  case class Listen(path:Path) extends Command

  case class Read(path:Path) extends Command

}

package object statuses {

  import org.albert.message.Status

  case class WhatIsYourStatus() extends Status

  case class ImWorking() extends Status

  case class ImRestarting() extends Status

  case class TryingToStopMySelf() extends Status

  case class ReadingConfig() extends Status

  case class InitiatingSequence() extends Status

  case class WaitingForChildren() extends Status

  case class WaitingForPath() extends Status
}

package object suite {

  import akka.actor.ActorRef
  import com.typesafe.scalalogging.LazyLogging
  import org.albert.message.SuiteStep

  case class SequentialStep(suite:ActorRef) extends SuiteStep

  case class ParallelStep(suite:ActorRef) extends SuiteStep

  case class SuiteOrder(parallelSuite:Seq[ActorRef] = Seq.empty[ActorRef], sequentialSuite:Seq[ActorRef] = Seq.empty[ActorRef]) extends LazyLogging {

     def +[T <: SuiteStep](subSuite:T):SuiteOrder = {
       subSuite match {
         case ParallelStep(actor) => SuiteOrder(parallelSuite :+ actor, sequentialSuite)
         case SequentialStep(actor) => SuiteOrder(parallelSuite, sequentialSuite :+ actor)
       }
     }

    def -[T <: SuiteStep](subSuite:T):SuiteOrder = {
      subSuite match {
        case ParallelStep(actor) => SuiteOrder(parallelSuite filterNot (actor equals) , sequentialSuite)
        case SequentialStep(actor) => SuiteOrder(parallelSuite, sequentialSuite filterNot (actor equals))
      }
    }

    def foreach[U](p:ActorRef => U) = {
      (sequentialSuite ++ parallelSuite) foreach p
    }

    def nextSequential(p:ActorRef => Unit):SuiteOrder ={
      (sequentialSuite headOption) foreach p
      SuiteOrder( parallelSuite,sequentialSuite init )
    }

    def nextParallel(p:ActorRef => Unit):SuiteOrder ={
      (parallelSuite headOption) foreach  p
      SuiteOrder( parallelSuite init, sequentialSuite)
    }

    def merge(subSuite:SuiteOrder) = {
      SuiteOrder(parallelSuite ++ (subSuite parallelSuite), sequentialSuite ++ (subSuite sequentialSuite))
    }

    def isEmpty = {
      parallelSuite.isEmpty && sequentialSuite.isEmpty
    }

  }
}

package object actor {

  import akka.persistence.PersistentActor
  import akka.actor.{Actor, ActorLogging, ActorRef, Props}
  import com.beachape.filemanagement.MonitorActor
  import scala.reflect.io.{Directory, File, Path}


  trait AlbertActor extends Actor with ActorLogging

  class AlbertFSM[S,D] extends Actor with LoggingFSM[S, D]

  trait AlbertProcessor extends PersistentActor with ActorLogging

  trait AlbertMonitor extends MonitorActor with ActorLogging

  trait ActorObject {
    self =>

    def apply() = props(None)

    def apply(actorRef: ActorRef) = props(Some(actorRef))

    def props(actorRef:Option[ActorRef]):Props = actorRef map{
      value =>
        Props(self.getClass, value)
    } getOrElse{ Props(self.getClass) }
    
  }

  implicit class DirectoryOps(it:Directory) {

    def findSuiteFile(filter:Directory => Option[Path]):Option[File] = {
      (it ifDirectory filter) flatMap (value => value map (_ toFile))
    }

    def checkSubSuiteDirs(filter:Directory => Option[Path]):Set[Directory] = {
      ((it dirs) toSet) filter ( dir => (filter(dir) fold false)(_ => true) )
    }

  }

  def validate[T:Validator](path:T):Either[T,Exception] = {
    implicitly[Validator[T]] validate path
  }

  implicit object SuiteDirNameValidator extends Validator[String] {
    override def validate(arg: String): Either[Path, Exception] = {
      Left(arg) // TODO: need correct validation
    }
  }

  implicit object PathValidator extends Validator[Path] {
    override def validate(arg: Path): Either[Path, Exception] = {
      Left(arg) // TODO: need correct validation
    }
  }

}
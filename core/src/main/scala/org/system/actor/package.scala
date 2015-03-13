package org.system

import org.system.suite.SuiteConfig

import scala.language.{implicitConversions, postfixOps}

/**
 * Created by nutscracker on 6/30/2014.
 */

package object message {

  sealed trait Message

  trait Command extends Message

  trait Status extends Message

  trait State extends Message

  trait SuiteStep extends Message

  trait PluginMessage extends Message

}

package object command {

  import akka.actor.PossiblyHarmful
  import org.system.message.Command

  import scala.reflect.io.{File, Path}

  case class WrongSuitePath(path: Path) extends Command

  case class ParsedConfig(suiteConfig:SuiteConfig) extends Command

  case class Start(path: Path) extends Command

  case class Watch(path: Path) extends Command

  case class Restart(path: Path) extends Command with PossiblyHarmful

  case object Stop extends Command with PossiblyHarmful

  case object WorkCompleted extends Command

  case object SuiteCompleted extends Command

}

package object plugin {

  case class PluginScript(script:String)

  case class PluginType(pluginName:String)

  case class PluginCommand(pluginType:PluginType, pluginScript: PluginScript)

}

package object statuses {

  import org.system.message.Status

  case object WhatIsYourStatus extends Status

  case object ImWorking extends Status

  case object ImRestarting extends Status

  case object TryingToStopMySelf extends Status

  case object ReadingConfig extends Status

  case object InitiatingSequence extends Status

  case object WaitingForSlaves extends Status

  case object WaitingForPath extends Status

}

package object suite {

  import akka.actor.ActorPath
  import org.system.message.SuiteStep
  import scala.reflect.io.File

  case class ParallelStep(suite: ActorPath) extends SuiteStep

  case class ExcelFile(file:File)

  case class PropertiesFile(file:File)

  case class SuiteConfig(excel:ExcelFile, propertiesFile: PropertiesFile)

}

package object actor {

  import akka.actor.{Actor, ActorLogging, ActorRef, LoggingFSM, Props}
  import akka.camel.{Consumer, Producer}
  import akka.persistence.PersistentActor
  import com.beachape.filemanagement.MonitorActor
  import org.system.message.Message

  import scala.reflect.io.Path

  object withProps {

    import scala.reflect.{ClassTag, classTag}

    def apply[T: ClassTag](): Props = props[T](None)

    def apply[T: ClassTag](actorRef: ActorRef): Props = props(Some(actorRef))

    def apply[T: ClassTag](concurrency: Int): Props = Props(classTag[T] runtimeClass, concurrency)

    def apply[T: ClassTag](path: Path): Props = Props(classTag[T] runtimeClass, path)

    def apply[T: ClassTag](actorRef: ActorRef, path: Path): Props = Props(classTag[T] runtimeClass, actorRef, path)


    def props[T: ClassTag](actorRef: Option[ActorRef]): Props = actorRef map {
      value =>
        Props(classTag[T] runtimeClass, value)
    } getOrElse {
      Props(classTag[T] runtimeClass)
    }

  }

  trait SystemCamelProducerActor extends SystemActor with Producer {

    def queueType: String

  }

  trait SystemCamelConsumerActor extends SystemActor with Consumer

  trait SystemActor extends Actor with ActorLogging

  trait SystemActorProcessor extends PersistentActor with ActorLogging

  trait SystemActorMonitor extends MonitorActor with ActorLogging

  class SystemActorFSM[S, D] extends Actor with LoggingFSM[S, D]

}

package object implicits {

  import java.nio.file.{Path => JavaPath}

  import akka.actor.Actor.Receive

  import scala.reflect.io.{Directory, File, Path => ScalaPath}

  implicit class ReceiveOps(val receive: Receive) extends AnyVal

  implicit class PathOps(val path: ScalaPath) extends AnyVal {

    def toDirOrParentDir(): Directory = path ifFile (_ parent) getOrElse (path toDirectory)

    def filesAndDirs(): (Iterator[File], Iterator[Directory]) = {
      val dir = toDirOrParentDir()
      (dir files) -> (dir dirs)
    }

    def filesAndDirs(forFiles: File => Unit)(forDirs: Directory => Unit) {
      val dir = toDirOrParentDir()
      (dir files) foreach forFiles
      (dir dirs) foreach forDirs
    }

  }

  implicit class FileOps(val it: File) extends AnyVal {

    def isRequiredFile: Boolean = requiredFileNames() exists (((it name) trim) eq)

  }

  implicit class DirectoryOps(val it: Directory) extends AnyVal {

    def requiredFiles(): Iterator[File] = (it files) filter (_ isRequiredFile)

    def hasRequiredFiles: Boolean = (it files) filter (_ isRequiredFile) nonEmpty

    def hasNoRequiredFiles: Boolean = !hasRequiredFiles

    def findSlaveSuites(): Seq[Directory] = (it dirs) filter (_ requiredFiles() nonEmpty) toSeq

  }

  implicit object SuiteDirNameValidator extends Validator[String] {

    override def validate(arg: String): Either[Throwable, String] = {
      Right(arg) // TODO: need correct validation
    }

  }

  implicit object PathValidator extends Validator[ScalaPath] {

    override def validate(arg: ScalaPath): Either[Throwable, ScalaPath] = {
      Right(arg) // TODO: need correct validation
    }

  }

  implicit def fileToJFile(path: ScalaPath): JavaPath = (path jfile) toPath

  implicit def java2ScalaPath(path: java.nio.file.Path): ScalaPath = ScalaPath((path toAbsolutePath) toString)

  implicit def java2ScalaPathOps(path: java.nio.file.Path): PathOps = PathOps(path)
}
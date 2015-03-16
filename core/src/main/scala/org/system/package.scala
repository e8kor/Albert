package org

import akka.camel.CamelExtension
import com.typesafe.config.Config
import org.apache.activemq.camel.component.ActiveMQComponent
import org.system._

import scala.language.{implicitConversions, postfixOps}

/**
 * Created by nutscracker on 6/29/2014.
 */
package object system {

  import akka.actor.ActorSystem
  import com.typesafe.config.ConfigFactory

  import scala.reflect.runtime.universe._

  trait Validator[B] {

    def validate(arg: B): Either[Throwable, B]

  }

  private implicit val config = ConfigFactory load()
  private val freeTextNS = "system.freetext."
  private val defaultsNS = "system.defaults."

  def requiredFileNames():List[String] = lookForValue[List[String]](defaultsNS concat "requiredFiles")

  def freeText(path:String):String = lookForValue[String](freeTextNS concat path)

  def default(path:String):String = lookForValue[String](defaultsNS concat path)

  def lookForValue[T:TypeTag](path:String)(implicit config: Config):T = {
    val typeTag = typeOf[T]
    val result = if (typeTag =:= typeOf[String]) {
      config getString path
    } else if (typeTag =:= typeOf[List[String]]) {
      config getStringList path
    }
    (result asInstanceOf)[T]
  }

  def prepareCamel(system: ActorSystem) {
    val camel = CamelExtension(system)
    (camel context) removeComponent default("mqComponent")
    (camel context) addComponent(default("mqComponent"), ActiveMQComponent activeMQComponent default("mqURL"))
  }
}

package object implicits {

  import java.nio.file.{Path => JavaPath}

  import akka.actor.Actor.Receive

  import scala.reflect.io.{Directory, File, Path => ScalaPath}

  implicit class ReceiveOps(val receive: Receive) extends AnyVal

  implicit class PathOps(val path: ScalaPath) extends AnyVal {

    def dirOrParentDir(): Directory = path ifFile (_ parent) getOrElse (path toDirectory)

    def filesAndDirs(): (Iterator[File], Iterator[Directory]) = {
      val dir = dirOrParentDir()
      (dir files) -> (dir dirs)
    }

    def filesAndDirs(forFiles: File => Unit)(forDirs: Directory => Unit) {
      val dir = dirOrParentDir()
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


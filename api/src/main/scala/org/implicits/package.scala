package org

import com.typesafe.config.Config
import org.system._

import scala.language.postfixOps
import scala.util.Try

/**
 * Created by evgeniikorniichuk on 24/03/15.
 */
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

  implicit class ConfigOps(val config:Config) extends AnyVal {
    
    def findClass(path:String):Option[Class[_]] = {
      implicit val implicitConfig = config
        Try {
          Class forName lookForValue[String](path)
        } toOption
    }

    def getClass(path:String):Class[_] = {
      findClass(path) getOrElse(sys error(freeText("classNotFound") format path))
    }
  }
  
  implicit class FileOps(val it: File) extends AnyVal {

    import system.{requiredFileNames, requiredRootConfig}

    def isRequiredFile: Boolean = requiredFileNames() exists (((it name) trim) eq)

    def isRootConfig: Boolean = requiredRootConfig() eq (it name)
  }

  implicit class DirectoryOps(val it: Directory) extends AnyVal {

    import system.default

    def requiredFiles(): Iterator[File] = (it files) filter (_ isRequiredFile)

    def hasRequiredFiles: Boolean = requiredFiles() nonEmpty

    def hasNoRequiredFiles: Boolean = requiredFiles() isEmpty

    def rootConfig(): Option[File] = (it files) find (_ isRootConfig)

    def findSlaveSuites(): Seq[Directory] = (it dirs) filter (_ requiredFiles() nonEmpty) toSeq

    def suiteConfig(): Option[File] = (it files) find (file => (file name) equalsIgnoreCase default("suiteConfig"))

  }

  implicit object SuiteDirNameValidator extends system.Validator[String] {

    override def validate(arg: String): Either[Throwable, String] = {
      Right(arg) // TODO: need correct validation
    }

  }

  implicit object PathValidator extends system.Validator[ScalaPath] {

    override def validate(arg: ScalaPath): Either[Throwable, ScalaPath] = {
      Right(arg) // TODO: need correct validation
    }

  }

  implicit def fileToJFile(path: ScalaPath): JavaPath = (path jfile) toPath

  implicit def java2ScalaPath(path: java.nio.file.Path): ScalaPath = ScalaPath((path toAbsolutePath) toString)

  implicit def java2ScalaPathOps(path: java.nio.file.Path): PathOps = PathOps(path)
}


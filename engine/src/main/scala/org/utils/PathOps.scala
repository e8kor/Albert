package org.utils

import java.nio.file.{Path => JavaPath}

import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path => ScalaPath}

class PathOps(val path: ScalaPath) extends AnyVal {

  def filesAndDirs(): (Iterator[File], Iterator[Directory]) = {
    val dir = dirOrParentDir()
    (dir files) -> (dir dirs)
  }

  def dirOrParentDir(): Directory = path ifFile (_ parent) getOrElse (path toDirectory)

}

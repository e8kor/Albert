package org.utils

import java.nio.file.{Path => JavaPath}

import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path => ScalaPath}

class PathOps(val path: ScalaPath) extends AnyVal {

  import org.utils.implicits.{dir2DirOps, file2FileOps}

  def getSuiteCallbacks = {
    val (files, dirs) = filesAndDirs()

    (files map (_ withFileCallback())) ++ (dirs map (_ withDirCallback()))
  }

  def filesAndDirs(): (Iterator[File], Iterator[Directory]) = {
    val dir = dirOrParentDir()
    (dir files) -> (dir dirs)
  }

  def dirOrParentDir(): Directory = path ifFile (_ parent) getOrElse (path toDirectory)

}

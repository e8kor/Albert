package org.utils

import java.nio.file.{Path => JavaPath}

import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path => ScalaPath}
/**
 * Created by evgeniikorniichuk on 17/04/15.
 */
class PathOps(val path: ScalaPath) extends AnyVal {

  import org.implicits.{dir2DirOps, file2FileOps}

  def dirOrParentDir(): Directory = path ifFile (_ parent) getOrElse (path toDirectory)

  def filesAndDirs(): (Iterator[File], Iterator[Directory]) = {
    val dir = dirOrParentDir()
    (dir files) -> (dir dirs)
  }

  def getSuiteCallbacks = {
    filesAndDirs() match {
      case (files, dirs) =>
        (files map (_ withFileCallback())) ++ (dirs map (_ withDirCallback()))
    }
  }

  def filesAndDirs(forFiles: File => Unit)(forDirs: Directory => Unit) {
    val dir = dirOrParentDir()
    (dir files) foreach forFiles
    (dir dirs) foreach forDirs
  }

}

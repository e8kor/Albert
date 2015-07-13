package org

import com.typesafe.config.Config
import org.utils.{ConfigOps, DirectoryOps, FileOps, PathOps}

import scala.language.{implicitConversions, postfixOps}
import scala.reflect.io.{Directory, File}

/**
 * Created by evgeniikorniichuk on 24/03/15.
 */
package object implicits {

  import java.nio.file.{Path => JavaPath}

  import scala.reflect.io.{Path => ScalaPath}

  implicit def scala2JavaPath(path: ScalaPath): JavaPath = (path jfile) toPath

  implicit def java2ScalaPath(path: JavaPath): ScalaPath = ScalaPath((path toAbsolutePath) toString)

  implicit def java2ScalaPathOps(path: JavaPath): PathOps = new PathOps(path)

  implicit def dir2DirOps(dir:Directory): DirectoryOps = new DirectoryOps(dir)

  implicit def file2FileOps(file:File): FileOps = new FileOps(file)

  implicit def path2PathOps(path:ScalaPath): PathOps = new PathOps(path)

  implicit def config2ConfigOps(config:Config):ConfigOps = new ConfigOps(config)
}


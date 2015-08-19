package org.utils

import org.utils.implicits.dir2DirOps

import scala.concurrent.duration.FiniteDuration
import scala.reflect.io.Directory

case class RootConfig(
                       rootDirectory: Directory,
                       publishStatusIntialDelay: FiniteDuration,
                       publishStatusInterval: FiniteDuration,
                       autoStart: Boolean
                     ) {

  val suiteDirs = rootDirectory zipDirsByFile "suite.conf"

  require(suiteDirs nonEmpty,
    s"""illegal config: no suites found
        |path: ${rootDirectory path}""")

}

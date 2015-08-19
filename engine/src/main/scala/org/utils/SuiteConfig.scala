package org.utils

import org.utils.implicits.dir2DirOps

import scala.reflect.io.Directory

case class SuiteConfig(
                        suiteDirectory: Directory,
                        runnerParallelExecution: Boolean,
                        pluginClasses: Seq[Class[_]]
                      ) {

  val suiteDirs = suiteDirectory zipDirsByFile "suite.conf"

}

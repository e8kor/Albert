package org

import com.typesafe.scalalogging.LazyLogging

import scala.reflect.io.{Path, File}


/**
 * Created by nutscracker on 6/29/2014.
 */
package object albert{

  val suiteFileName = "suite.xls"
  val albert_root = "ALB_ROOT"

  implicit def fileToJFile(path:Path) = (path jfile) toPath

  trait Validator[B] {
    def validate(arg:B):Either[B,Exception]
  }

}


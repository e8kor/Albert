package org.system

import scala.reflect.io.File

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object suite {

  trait SystemConfig

   case class ExcelFile(file: File)

   case class PropertiesFile(file: File)

   case class SuiteConfig(excel: ExcelFile, propertiesFile: PropertiesFile) extends SystemConfig

 }

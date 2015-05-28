package org.system.plugin

import com.typesafe.config.ConfigFactory

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 01/05/15.
 */
package object rtp {

  private implicit val config = ConfigFactory load()
  private val freeTextNS = "system.rtp.freetext."
  private val defaultsNS = "system.rtp.defaults."
  private val suiteNS = "system.suite."

  def requiredFileNames(): List[String] = lookForValue[List[String]](defaultsNS concat "requiredFiles")

  def requiredRootConfig(): String = lookForValue[String](defaultsNS concat "rootConfig")

  def freeText(path: String): String = lookForValue[String](freeTextNS concat path)

  def default(path: String): String = lookForValue[String](defaultsNS concat path)

  def suite(path: String): String = lookForValue[String](suiteNS concat path)

  def lookForValue[T: ClassTag](path: String)(implicit config: Config): T = {
    val typeTag = classTag[T] runtimeClass

    val result = if ((typeTag isInstanceOf)[String]) {
      config getString path
    } else if ((typeTag isInstanceOf)[List[String]]) {
      config getStringList path
    }
    (result asInstanceOf)[T]
  }

}

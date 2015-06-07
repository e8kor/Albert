package org

import akka.camel.CamelExtension
import com.typesafe.config.Config
import org.apache.activemq.camel.component.ActiveMQComponent

import scala.language.{implicitConversions, postfixOps}
import scala.reflect.{ClassTag, classTag}

/**
 * Created by nutscracker on 6/29/2014.
 */
package object system {

  import akka.actor.ActorSystem
  import com.typesafe.config.ConfigFactory

  private implicit val config = ConfigFactory load()
  private val freeTextNS = "system.freetext."
  private val defaultsNS = "system.defaults."
  private val suiteNS = "system.suite."

  def requiredFileNames(): List[String] = lookForValue[List[String]](defaultsNS concat "requiredFiles")

  def requiredRootConfig(): String = lookForValue[String](defaultsNS concat "rootConfig")

  def freeText(path: String): String = lookForValue[String](freeTextNS concat path)

  def default(path: String): String = lookForValue[String](defaultsNS concat path)

  def suite(path: String): String = lookForValue[String](suiteNS concat path)

  def lookForValue[T: ClassTag](path: String)(implicit config: Config): T = {
    val typeTag = classTag[T] runtimeClass
    val stringClass = classOf[String]
    val listClass = classOf[List[String]]

    val result = typeTag match {
      case `stringClass` => config getString path
      case `listClass` => config getStringList path
    }

    (result asInstanceOf)[T]
  }

  def prepareCamel(system: ActorSystem): Unit = {
    val camel = CamelExtension(system)
    (camel context) removeComponent default("mqComponent")
    (camel context) addComponent (default("mqComponent"), ActiveMQComponent activeMQComponent default("mqURL"))
  }

}



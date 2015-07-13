package org

import akka.camel.CamelExtension
import com.typesafe.config.Config
import org.apache.activemq.camel.component.ActiveMQComponent

import scala.language.{implicitConversions, postfixOps}
import scala.reflect.{ClassTag, classTag}

package object system {

  import akka.actor.ActorSystem

//  private val freeTextNS = "system.freetext."
//  private val defaultsNS = "system.defaults."
//  private val suiteNS = "system.suite."

  //  def requiredFileNames()(implicit config: Config): List[String] = lookForValue[List[String]](defaultsNS concat "requiredFiles")

  //  def requiredRootConfig()(implicit config: Config): String = lookForValue[String](defaultsNS concat "rootConfig")

  //  def freeText(path: String)(implicit config: Config): String = lookForValue[String](freeTextNS concat path)

//  def prepareCamel(system: ActorSystem)(implicit config: Config) = {
//    val camel = CamelExtension(system)
//    (camel context) removeComponent ??? /* TODO : need to provide properly default("mqComponent")*/
//    (camel context) addComponent(??? /* TODO : need to provide properly default("mqComponent")*/ , ActiveMQComponent activeMQComponent default("mqURL"))
//    camel
//  }

  //  def suite(path: String)(implicit config: Config): String = lookForValue[String](suiteNS concat path)

//  def default(path: String)(implicit config: Config): String = lookForValue[String](defaultsNS concat path)

//  def lookForValue[T: ClassTag](path: String)(implicit config: Config): T = {
//    val typeTag = classTag[T] runtimeClass
//    val stringClass = classOf[String]
//    val listClass = classOf[List[String]]
//
//    val result = typeTag match {
//      case `stringClass` => config getString path
//      case `listClass` => config getStringList path
//    }
//
//    (result asInstanceOf)[T]
//  }

}



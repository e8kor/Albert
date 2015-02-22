package org

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

import scala.language.{implicitConversions, postfixOps}
import scala.reflect.runtime.universe._


/**
 * Created by nutscracker on 6/29/2014.
 */
package object system {

  trait Validator[B] {
    def validate(arg: B): Either[Throwable, B]
  }

  private val config = ConfigFactory load()
  private val freeTextNS = "system.freetext."
  private val defaultsNS = "system.defaults."

  lazy val actorSystem = ActorSystem create default("systemName")

  def requiredFileNames():List[String] = lookForValue[List[String]](defaultsNS concat "requiredFiles")

  def freeText(path:String):String = lookForValue[String](freeTextNS concat path)

  def default(path:String):String = lookForValue[String] (defaultsNS concat path)

  private def lookForValue[T:TypeTag](path:String):T = {
    val typeTag = typeOf[T]
    if (typeTag =:= typeOf[String]) {
      (config getString path) asInstanceOf
    }else if (typeTag =:= typeOf[List[String]]) {
      (config getStringList path) asInstanceOf
    } else {
      sys error(config getString (freeTextNS concat "unsupportedType"))
    }
  }

}


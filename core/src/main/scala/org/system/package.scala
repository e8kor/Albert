package org

import com.typesafe.config.Config

import scala.language.postfixOps

/**
 * Created by nutscracker on 6/29/2014.
 */
package object system {

  import akka.actor.ActorSystem
  import com.typesafe.config.ConfigFactory

  import scala.reflect.runtime.universe._

  trait Validator[B] {

    def validate(arg: B): Either[Throwable, B]

  }

  private implicit val config = ConfigFactory load()
  private val freeTextNS = "system.freetext."
  private val defaultsNS = "system.defaults."

  lazy val actorSystem = ActorSystem create default("systemName")

  def requiredFileNames():List[String] = lookForValue[List[String]](defaultsNS concat "requiredFiles")

  def freeText(path:String):String = lookForValue[String](freeTextNS concat path)

  def default(path:String):String = lookForValue[String](defaultsNS concat path)

  def lookForValue[T:TypeTag](path:String)(implicit config: Config):T = {
    val typeTag = typeOf[T]
    val result = if (typeTag =:= typeOf[String]) {
      config getString path
    } else if (typeTag =:= typeOf[List[String]]) {
      config getStringList path
    }
    (result asInstanceOf)[T]
  }

}


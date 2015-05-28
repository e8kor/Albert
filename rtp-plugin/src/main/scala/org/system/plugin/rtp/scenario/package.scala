package org.system.plugin.rtp

import com.typesafe.config.Config
import info.folone.scala.poi.{Cell, Row}

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 23/04/15.
 */
package object scenario {

  implicit object RowOrdering extends Ordering[Row] {
    override def compare(x: Row, y: Row): Int = {
      (x index) compareTo (y index)
    }
  }

  implicit object CellOrdering extends Ordering[Cell] {
    override def compare(x: Cell, y: Cell): Int = {
      (x index) compareTo (y index)
    }
  }

  implicit class ConfigResolve(val it:Config) extends AnyVal {

    def resolveValue:PartialFunction[String,String] = {
      case key:String if key startsWith "$" => it getString key
      case other:String => other
    }

  }
}

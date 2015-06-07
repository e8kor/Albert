package org.system.plugin.rtp.scenario

import java.net.URL

import info.folone.scala.poi.Cell
import org.system.scenario.TransportConfiguration
import org.system.types.{Password, Username}

import scala.concurrent.duration.Duration


/**
 * Created by evgeniikorniichuk on 23/03/15.
 */

object DBConfig extends Excels {

  val dbColumns = Seq("ID", "Type", "Class", "Description", "JdbcUrl", "Username", "Password", "WaitTime")

  def fitsConfig(cells: Seq[Cell]): Boolean = {
    val stringCells = stringCellsFromRow(cells)
    getValuesForStringCells(stringCells) equals dbColumns
  }

  def apply(args: Seq[String]) = {
    ???
  }

}

case class DBConfig(
                     url: URL,
                     username: Username,
                     password: Password,
                     waitTime: Duration
                     ) extends TransportConfiguration

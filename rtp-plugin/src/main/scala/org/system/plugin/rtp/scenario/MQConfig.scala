package org.system.plugin.rtp.scenario

import java.net.URL

import info.folone.scala.poi.Cell
import org.system.scenario.TransportConfiguration
import org.system.{Password, Username}

import scala.concurrent.duration.Duration

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */

object MQConfig extends Excels {

  val mqColumns = Seq("ID", "Type", "Class", "Description", "QueueManager", "HostName", "Queue", "Channel", "Port", "Username", "Password", "Encoding", "WaitTime")

  def fitsConfig(cells: Seq[Cell]): Boolean = {
    val stringCells = stringCellsFromRow(cells)
    getValuesForStringCells(stringCells) equals mqColumns
  }

  def apply(args: Seq[String]) = {
    ???
  }

}

case class MQConfig(
                     queueManager: String,
                     url: URL,
                     queue: String,
                     channel: String,
                     port: Int,
                     username: Username,
                     password: Password,
                     encoding: String,
                     waitTime: Duration
                     ) extends TransportConfiguration

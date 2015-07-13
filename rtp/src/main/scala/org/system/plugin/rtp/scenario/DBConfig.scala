package org.system.plugin.rtp.scenario

import java.net.URL

import com.typesafe.scalalogging.LazyLogging
import info.folone.scala.poi.Cell
import org.system.scenario.TransportConfiguration
import org.system.types.{Password, Username}

import scala.concurrent.duration.Duration

object Config extends Excels with LazyLogging {

  val dbColumns = Seq("ID", "Type", "Class", "Description", "JdbcUrl", "Username", "Password", "WaitTime")

  val mqColumns = Seq("ID", "Type", "Class", "Description", "QueueManager", "HostName", "Queue", "Channel", "Port", "Username", "Password", "Encoding", "WaitTime")

  def fitsConfig(cells: Seq[Cell]): Boolean = {
    getValuesForStringCells(stringCellsFromRow(cells)) equals dbColumns
  }

  def apply(args: Seq[Seq[String]]) = {

    args map {
      case id :: tpe :: clazz :: description :: jdbcUrl :: uname :: pwd :: waitTime :: Nil =>
        DBConfig(new URL(jdbcUrl), uname, pwd, Duration(waitTime), ConnectionType withName tpe)
      case id :: tpe :: clazz :: description :: queueManager :: host :: queue :: chnl :: port :: uname :: pwd :: encoding :: waitTime :: Nil =>
      case other => logger warn s"unmatched config args : ${other mkString ","}"
    }
  }

}

case class DBConfig(url: URL,
                    username: Username,
                    password: Password,
                    waitTime: Duration,
                    connectionType: ConnectionType.Value) extends TransportConfiguration

case class MQConfig(queueManager: String,
                    url: URL,
                    queue: String,
                    channel: String,
                    port: Int,
                    username: Username,
                    password: Password,
                    encoding: String,
                    waitTime: Duration,
                    connectionType: ConnectionType.Value) extends TransportConfiguration
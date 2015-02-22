package org.system
package actor

import akka.actor.{ActorRef, PoisonPill, Terminated}
import org.system.command.{ParsedConfig, WrongSuitePath}
import org.system.implicits.{DirectoryOps, PathOps, ReceiveOps}
import org.system.suite.{PropertiesFile, ExcelFile, SuiteConfig}

import scala.language.postfixOps
import scala.reflect.io.{Directory, File, Path}

/**
 * Created by nutscracker on 7/22/2014.
 */
class ConfigReader(suiteManager: ActorRef, dir: Directory) extends SystemActor {

  context watch suiteManager
  val files = dir requiredFiles()
  suiteManager ! (if (files nonEmpty) parseFile(files) else WrongSuitePath(dir))

  override def receive = normal

  private def normal: Receive = {
    case Terminated(actorRef) ⇒
      log info freeText("terminatingConfigReader")
      self ! PoisonPill
  } |: other

  def parseFile(files: Iterator[File]): ParsedConfig = {
    val excel = files.find( file => (file extension ) eq default("excelExtension")) getOrElse(sys error freeText("noExcelFile"))
    val properties = files.find( file => (file extension ) eq "properties")getOrElse(sys error freeText("noProps File"))
    ParsedConfig(SuiteConfig(ExcelFile(excel), PropertiesFile(properties)))
  }

}

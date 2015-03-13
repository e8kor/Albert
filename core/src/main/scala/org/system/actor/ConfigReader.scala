package org.system
package actor

import akka.actor.{ActorRef, PoisonPill, Terminated}
import org.system.command.{ParsedConfig, WrongSuitePath}
import org.system.implicits.DirectoryOps
import org.system.suite.{ExcelFile, PropertiesFile, SuiteConfig}

import scala.language.postfixOps
import scala.reflect.io.{Directory, File}

/**
 * Created by nutscracker on 7/22/2014.
 */
class ConfigReader(suiteManager: ActorRef, dir: Directory) extends SystemActor {

  suiteManager ! {
    dir requiredFiles() find {
      case file if (file extension) eq default("excelExtension") => true
    } flatMap {
      excel =>
        dir requiredFiles() find { case file if (file extension) eq "properties" => true} map {
          props =>
            excel -> props
        }
    } map {
      case (excel, properties) =>
        ParsedConfig(SuiteConfig(ExcelFile(excel), PropertiesFile(properties)))
    } getOrElse WrongSuitePath(dir)
  }

  override def receive = normal

  private def normal: Receive = {
    case Terminated(actorRef) ⇒
      log info freeText("terminatingConfigReader")
      self ! PoisonPill
  }

  def parseFile(files: Iterator[File]) = {
    (context parent) ! {
      files find {
        case file if (file extension) eq default("excelExtension") => true
      } flatMap {
        excel =>
          files find { case file if (file extension) eq "properties" => true} map {
            props =>
              excel -> props
          }
      } map {
        case (excel, properties) =>
          ParsedConfig(SuiteConfig(ExcelFile(excel), PropertiesFile(properties)))
      } getOrElse WrongSuitePath(dir)
    }
  }

}

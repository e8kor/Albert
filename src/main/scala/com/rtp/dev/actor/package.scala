package com.rtp.dev

import java.io.File

import akka.actor.{Actor, ActorLogging}
import akka.persistence.Processor
import com.beachape.filemanagement.MonitorActor

import scalaz._

/**
 * Created by nutscracker on 6/30/2014.
 */
package object actor {

  trait ConfigFile

  trait RTPConfig

  trait Executable

  trait Context

  trait Bundle

  trait RTActor extends Actor with ActorLogging

  trait RTProcessor extends Processor with ActorLogging

  trait RTMonitorActor extends MonitorActor with ActorLogging

  case object NoFoundConfig extends RTPConfig

  case class PreparedConfig (mFile:MacroFile, eFile:ExcelFile) extends RTPConfig

  case class ExecutionScope (tasks:Seq[Task], bundle:Bundle)

  case class Task (context:Context, script:Script)

  type MacroFile = File @@ ConfigFile

  type ExcelFile = File @@ ConfigFile

  type Script    = String @@ Executable

}
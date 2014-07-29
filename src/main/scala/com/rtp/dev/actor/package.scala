package com.rtp.dev

import java.io.File
import java.nio.file.Path

import akka.actor.{Actor, ActorLogging}
import akka.persistence.Processor
import com.beachape.filemanagement.Messages.RegisterCallback
import com.beachape.filemanagement.MonitorActor

import scalaz._
import Scalaz._

/**
 * Created by nutscracker on 6/30/2014.
 */
package object actor {

  trait RTActor extends Actor with ActorLogging
  
  trait RTProcessor extends Processor with ActorLogging

  trait RTMonitorActor extends MonitorActor with ActorLogging

  trait ConfigFile

  trait RTPConfig

  trait Executable

  trait Context

  trait Bundle

  case object NoFoundConfig extends RTPConfig

  case class PreparedConfig(mFile:MacroFile, eFile:ExcelFile) extends RTPConfig

  case class Task(context:Context, script:Script)

  case class ExecutionScope(tasks:Seq[Task], bundle:Bundle)

  type MacroFile = File@@ConfigFile

  type ExcelFile = File@@ConfigFile

  type Script = String@@Executable

}
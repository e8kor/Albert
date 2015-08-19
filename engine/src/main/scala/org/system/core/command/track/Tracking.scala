package org.system.core.command.track

import java.util.Date

import akka.actor.ActorRef
import org.system.api.command.{Request, Response}

import scala.reflect.io.Directory

sealed trait Tracking extends Response

case object PublishStatus extends Request

case class SuiteExecutionStarted(actorRef: ActorRef, suiteDir: Directory, date: Date = new Date()) extends Tracking

case class SuiteExecutionCompleted(actorRef: ActorRef, suiteDir: Directory, date: Date = new Date()) extends Tracking

case class RunnerExecutionCompleted(actorRef: ActorRef, suiteDir: Directory, date: Date = new Date()) extends Tracking

sealed trait Status extends Tracking

case class AwaitStart(actorRef: ActorRef, suiteDir: Directory, date: Date = new Date()) extends Status

case class AwaitSuiteExecution(actorRef: ActorRef, suiteDir: Directory, date: Date = new Date()) extends Status

case class AwaitRunnersExecution(actorRef: ActorRef, suiteDir: Directory, date: Date = new Date()) extends Status
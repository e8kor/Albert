package org.albert
package actor

import akka.actor.PoisonPill

/**
 * Created by nutscracker on 6/30/2014.
 */
/*

import org.albert.execution._
import org.albert.task._

object ExecutionHandler extends ActorObject

class ExecutionHandler extends RTProcessor {

  import context.{watch, become, actorOf, parent}

  override def receive:Receive = normal

  def normal:Receive = {
    case ExecutionScope(tasks, bundle) =>
      become(aggregate(tasks, Map.empty))
      implicit val _bundle = bundle
      tasks map {
        task =>
          val worker = actorOf(Worker())
          watch(worker)
          worker ! prepareTask(task)
      }
    case _ =>
  }

  def aggregate(tasks:Seq[TaskToExecute], done:Map[TaskToExecute, TaskResult]):Receive = {
    case FinishedTask(task, result) =>
      val uTasks = tasks dropWhile (task equals)
      val uDone = done + (task -> result)
      if (checkTasksDone(uTasks, task)) {
        parent ! process(prepare(uDone))
        self ! PoisonPill
      } else {
        become(aggregate(uTasks, uDone))
      }
  }

  def checkTasksDone(tasks:Seq[TaskToExecute], doneTask:TaskToExecute): Boolean = ???

  def prepare(done:Map[TaskToExecute, TaskResult]): ExecutionResult = ???

  def process(result:ExecutionResult): Boolean = ???

  def prepareTask(task:TaskToExecute)(implicit bundle:Bundle):TaskToExecute = ???

}
*/
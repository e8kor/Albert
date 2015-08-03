package org.system.test

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.event.EventStream.fromActorSystem
import akka.testkit.{TestActorRef, TestProbe}
import org.system.core.actors.main.RootExecutor
import org.system.core.command.manage.StartSuite
import org.system.core.command.track.{SuiteExecutionStarted, SuiteExecutionCompleted, Tracking}
import org.system.test.SuiteConfigFiles._
import org.system.test.spec.SystemActorSpec

import scala.concurrent.duration._
import scala.language.postfixOps

class EventTestActor extends Actor with ActorLogging {

  import scala.collection.mutable

  val msgsExecCompleted = mutable.Queue[SuiteExecutionCompleted]()

  val msgsExecStarted = mutable.Queue[SuiteExecutionStarted]()
// TODO looks like suite completed not important for me, but
  override def receive: Receive = {
    case completed: SuiteExecutionCompleted =>
      log info s"received interesting for us message $completed"
      msgsExecCompleted += completed
    case initialized: SuiteExecutionStarted =>
      log info s"received interesting for us message $initialized"
      msgsExecStarted += initialized
    case other =>
      log info s"received not interesting for us message $other"
  }

}

class DryRunWithDeclaredFlow extends SystemActorSpec {

  val order = Set(
    Set(
      "suite1" -> Set(
        "suite11" -> Set[String](),
        "suite12" -> Set[String]()
      ),
      "suite2" -> Set(
        "suite21" -> Set(
          "suite211" -> Set[String](),
          "suite212" -> Set[String]()
        ),
        "suite22" -> Set[String]()
      )
    )
  )

  "Dry run system with SuiteInfo as Plugin Actor" must {

    val executorWatcher = TestProbe()

    val probeRef: TestActorRef[EventTestActor] = TestActorRef(Props(new EventTestActor), "EventTestActor")

    val rootExecutor = TestActorRef[RootExecutor](Props(RootExecutor(testSuite5Dir)),"TestRootActor")

    val state = system subscribe(probeRef, classOf[Tracking])

    if (state) {
      info(s" ${probeRef path} successfully subscribed on ${classOf[Tracking] getSimpleName} ")
    } else {
      info(s" ${probeRef path} failed to subscribe on ${classOf[Tracking] getSimpleName} ")
    }

    executorWatcher watch rootExecutor

    "execute successfully with only printing info about there they was instantiated " in {

      rootExecutor ! StartSuite

      awaitCond(p = {
        val termMessage = (executorWatcher expectMsgType)[Terminated](10 seconds)
        (termMessage isInstanceOf)[Terminated]
      }, message = "await for root executor terminated"
      )

      println(s"Starting order : \n ${((probeRef underlyingActor) msgsExecStarted) mkString ", "}")

      println(s"Completion order : \n ${((probeRef underlyingActor) msgsExecCompleted) mkString ", "}")

      assert(true, "dummy check")

      //      val msgsInitialized = probe expectMsgAllConformingOf classOf[Tracking]
      //      val msgsCompleted = probe expectMsgAllConformingOf classOf[Tracking]
      //      val initializeds = (probe receiveWhile()) {
      //        case initialized:SuiteInitialized =>
      //          println(s"received interesting for us message $initialized")
      //          Some(initialized)
      //        case other =>
      //          println(s"received not interesting for us message $other")
      //          None
      //      }
      //
      //      val completeds = (probe receiveWhile()) {
      //        case completed:SuiteExecutionCompleted =>
      //          println(s"received interesting for us message $completed")
      //          Some(completed)
      //        case other =>
      //          println(s"received not interesting for us message $other")
      //          None
      //      }
      //      println(s"Initialized order : \n ${(initializeds flatten) mkString ", "}")
      //      println(s"Completion order : \n ${(completeds flatten) mkString ", "}")

    }
  }
}

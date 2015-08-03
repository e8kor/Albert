package test

import akka.actor.{Props, Terminated}
import akka.testkit.TestProbe
import org.system.core.actors.main.RootExecutor
import org.system.core.command.manage.StartSuite

import scala.concurrent.duration._
import scala.language.postfixOps

class DryRunWithScriptRunners extends spec.SystemActorSpec {


  "Dry run system with SuiteInfo as Plugin Actor" must {

    import SuiteConfigFiles.testSuite4Dir

    val probe = TestProbe()
    val rootExecutor = system actorOf(
      Props[RootExecutor](RootExecutor(testSuite4Dir)),
      "TestRootActor2"
      )

    "execute successfully with only printing info about there they was instantiated " in {
      probe watch rootExecutor

      rootExecutor ! StartSuite

      assert(((probe expectMsgType)[Terminated](10 seconds) isInstanceOf)[Terminated])
    }

  }

  "Dry run system with ShellScriptExecutor as Plugin Actor" must {


    import SuiteConfigFiles.testSuite6Dir

    val probe = TestProbe()
    val rootExecutor = system actorOf(
      Props[RootExecutor](RootExecutor(testSuite6Dir)),
      "TestRootActor1"
      )

    "execute successfully with shell script executor was instantiated  and command executed" in {

      probe watch rootExecutor

      rootExecutor ! StartSuite

      assert(((probe expectMsgType)[Terminated](10 seconds) isInstanceOf)[Terminated])
    }

  }
}

package test

import akka.actor.{Props, Terminated}
import akka.testkit.TestProbe
import org.system.core.actors.main.RootExecutor
import org.system.core.command.manage.StartSuite

import scala.concurrent.duration._
import scala.language.postfixOps

class DryRunWithDeepStructure extends spec.SystemActorSpec {

  import SuiteConfigFiles.testSuite1Dir

  "Dry run system with SuiteInfo as Plugin Actor" must {

    val probe = TestProbe()
    val rootExecutor = system actorOf(
      Props[RootExecutor](RootExecutor(testSuite1Dir)),
      "RootActor"
      )

    "execute successfully with only printing info about there they was instantiated " in {
      probe watch rootExecutor

      rootExecutor ! StartSuite

      assert(((probe expectMsgType)[Terminated](10 seconds) isInstanceOf)[Terminated])
    }

  }
}

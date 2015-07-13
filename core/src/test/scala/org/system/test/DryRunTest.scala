package org.system.test

import akka.actor.{Props, Terminated}
import akka.testkit.TestProbe
import com.typesafe.config.ConfigFactory
import org.system.command.manage.{StartSuite, SuiteCompleted => ExecCompleted}
import org.system.core.actors.main.RootExecutor
import org.system.test.spec.SystemActorSpec

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.reflect.io.File

class DryRunTest extends SystemActorSpec {

  import SuiteConfigFiles.testSuite1

  "Dry run system with SuiteInfo as Plugin Actor" must {

    val probe = TestProbe()
    val rootExecutor = system actorOf(
      Props[RootExecutor](RootExecutor(ConfigFactory parseFile testSuite1 )),
      "RootActor"
      )

    "execute successfully with only printing info about there they was instantiated " in {
      probe watch rootExecutor

      rootExecutor ! StartSuite

      assert(((probe expectMsgType)[Terminated](10 seconds) isInstanceOf)[Terminated])
    }

  }
}

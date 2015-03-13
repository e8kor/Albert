package org.system.test

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKitBase, TestKit}
import org.scalatest.{BeforeAndAfterAll, FeatureSpec}

/**
 * Created by evgeniikorniichuk on 03/03/15.
 */
class SystemActorSpec(implicit override val system: ActorSystem = ActorSystem("SystemActorSpec")) extends FeatureSpec with TestKitBase  with ImplicitSender with BeforeAndAfterAll {

  override def afterAll() {
    TestKit.shutdownActorSystem(system)
  }

}

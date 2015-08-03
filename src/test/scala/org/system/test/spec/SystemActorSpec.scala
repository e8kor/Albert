package org.system
package test.spec

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.language.postfixOps

class SystemActorSpec(system: ActorSystem)
  extends TestKit(system)
  with ImplicitSender
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {

  def this() = this(ActorSystem create "AlbertTest")

  override def beforeAll() {
    //    prepareCamel(system)(ConfigFactory load())
  }

  override def afterAll() {
    TestKit shutdownActorSystem system
  }

}

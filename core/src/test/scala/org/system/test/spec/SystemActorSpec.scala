package org.system
package test.spec

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.language.postfixOps

/**
 * Created by evgeniikorniichuk on 03/03/15.
 */
class SystemActorSpec(system: ActorSystem)
  extends TestKit(system)
  with ImplicitSender
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll {

  def this() = this(ActorSystem create default("systemName"))

  override def beforeAll() {
    prepareCamel(system)
  }

  override def afterAll() {
    TestKit shutdownActorSystem system
  }

}

case class Message(test: String)
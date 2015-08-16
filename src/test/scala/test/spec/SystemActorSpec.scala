package test.spec

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

import scala.language.postfixOps

class SystemActorSpec(system: ActorSystem)
  extends TestKit(system)
  with ImplicitSender
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll
  with LazyLogging {

  def this() = this(ActorSystem create "AlbertTest")

  override def beforeAll(): Unit = {

  }

  override def afterAll(): Unit = {
    TestKit shutdownActorSystem system
  }

}

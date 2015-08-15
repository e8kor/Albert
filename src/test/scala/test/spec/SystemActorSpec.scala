package test.spec

import akka.actor.{ActorLogging, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}
import test.SuiteConfigFiles._

import scala.language.postfixOps

class SystemActorSpec(system: ActorSystem)
  extends TestKit(system)
  with ImplicitSender
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll
  with LazyLogging {

  def this() = this(ActorSystem create "AlbertTest")

  override def beforeAll() {

    logger info s"relative path $relativePath"
    //    prepareCamel(system)(ConfigFactory load())
  }

  override def afterAll() {
    TestKit shutdownActorSystem system
  }

}

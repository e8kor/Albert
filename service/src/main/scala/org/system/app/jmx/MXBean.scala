package org.system.app.jmx

import java.lang.management.ManagementFactory
import javax.management._

import akka.actor.Actor
import com.typesafe.config.{Config, ConfigFactory}
import org.system.app.command.jmx.StartRootExecutor
import org.system.core.actors.System.SystemActor

import scala.reflect.io.Directory


object MXBeanActor {

  def apply(config:Config = ConfigFactory empty()) = {
    new MXBeanActor(config)
  }

}

class MXBeanActor(config:Config) extends SystemActor with ActorJMX with MXBean {

  import akka.pattern.ask

  override def getMXTypeName: String = "ApplicationMXBean"

  override def startSuite(path: String): String = {
    self ? StartRootExecutor(Directory(path))
    "Request sent"
  }

  override def receive: Receive = {
    case any =>
      log warning "JMX Actor not responsible for receiving of messages for now"
  }

}

trait MXBean {

  def startSuite(path:String):String

}

trait ActorJMX {
  it:Actor =>

  protected lazy val objName: ObjectName = new ObjectName("mxbean", {
    import scala.collection.JavaConverters._
    new java.util.Hashtable(
      Map(
        "name" -> ((self path) toStringWithoutAddress),
        "type" -> getMXTypeName
      ).asJava
    )
  })

  def getMXTypeName: String

  override def preStart(): Unit = AkkaJMX registerToMBeanServer(this, objName)

  override def postStop(): Unit = AkkaJMX unregisterFromMBeanServer objName
}

object AkkaJMX {

  private lazy val mbs: MBeanServer = ManagementFactory getPlatformMBeanServer

  @throws[InstanceAlreadyExistsException]
  @throws[MBeanRegistrationException]
  @throws[RuntimeMBeanException]
  @throws[RuntimeErrorException]
  @throws[NotCompliantMBeanException]
  @throws[RuntimeOperationsException]
  def registerToMBeanServer(actor: Actor, objName: ObjectName): ObjectInstance = mbs registerMBean(actor, objName)

  @throws[RuntimeOperationsException]
  @throws[RuntimeMBeanException]
  @throws[RuntimeErrorException]
  @throws[InstanceNotFoundException]
  @throws[MBeanRegistrationException]
  def unregisterFromMBeanServer(objName: ObjectName): Unit = mbs unregisterMBean objName
}

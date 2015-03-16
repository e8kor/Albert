package org.system

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object actor {

  import akka.actor.{Actor, ActorLogging}
  import akka.camel.{Consumer, Producer}
  import com.beachape.filemanagement.MonitorActor

  type SystemActor = Actor with ActorLogging

  type SystemProducerActor = SystemActor with Producer

  type SystemConsumerActor = SystemActor with Consumer

  type SystemDirectoryMonitor = MonitorActor with ActorLogging

}
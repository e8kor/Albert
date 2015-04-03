package org.system.plugin

import akka.actor.{ActorLogging, Actor}

/**
 * Created by evgeniikorniichuk on 15/03/15.
 */
package object reader {
  
  trait ReaderActorPlugin extends Actor with ActorLogging

}

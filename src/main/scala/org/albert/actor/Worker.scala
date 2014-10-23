package org.albert
package actor

/**
 * Created by nutscracker on 6/30/2014.
 */

object Worker extends ActorObject

class Worker extends AlbertActor {

  override def receive: Receive = normal

  def normal: Receive = ???
}

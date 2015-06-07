package org.system.scenario

import java.net.URL

import org.system.types.{Password, Username}

import scala.concurrent.duration.Duration

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait TransportConfiguration {

  def url: URL

  def username: Username

  def password: Password

  def waitTime: Duration

}

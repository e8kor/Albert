package org.system.model.config

import java.net.URL

import org.system.{Password, Username}

import scala.concurrent.duration.Duration

/**
 * Created by evgeniikorniichuk on 23/03/15.
 */
trait TransportConfig {

  def url: URL

  def username: Username

  def password: Password

  def waitTime: Duration

}

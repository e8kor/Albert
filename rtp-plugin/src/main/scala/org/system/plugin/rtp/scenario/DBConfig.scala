package org.system.plugin.rtp.scenario

import java.net.URL
import org.system.scenario.TransportConfiguration
import org.system.{Password, Username}

import scala.concurrent.duration.Duration


/**
  * Created by evgeniikorniichuk on 23/03/15.
  */
case class DBConfig(
                         url: URL,
                         username: Username,
                         password: Password,
                         waitTime: Duration
                         ) extends TransportConfiguration

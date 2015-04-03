package org.system.plugin.rtp.transport

import java.net.URL

import org.system.{Password, Username}
import org.system.plugin.model.config.TransportConfig

import scala.concurrent.duration.Duration


/**
  * Created by evgeniikorniichuk on 23/03/15.
  */
case class DBConfig(
                         url: URL,
                         username: Username,
                         password: Password,
                         waitTime: Duration
                         ) extends TransportConfig

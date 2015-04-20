package org.system.plugin.rtp.scenario

import java.net.URL
import org.system.scenario.TransportConfiguration
import org.system.{Password, Username}

import scala.concurrent.duration.Duration

/**
  * Created by evgeniikorniichuk on 23/03/15.
  */

case class MQConfig(
                      queueManager: String,
                      url: URL,
                      queue: String,
                      channel: String,
                      port: Int,
                      username: Username,
                      password: Password,
                      encoding: String,
                      waitTime: Duration
                      ) extends TransportConfiguration

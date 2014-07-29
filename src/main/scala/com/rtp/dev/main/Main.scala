package com.rtp.dev.main

import java.nio.file.Paths

import com.rtp.Akka
import com.rtp.dev.actor.{RootExecutor, PathReader}
import akka.actor.ActorSystem
import com.rtp.dev._

import scalaz._
import Scalaz._

object Main extends App {

  import Akka._

  val root = system actorOf RootExecutor()

  val path = Paths get args(0)

  def start() = {
    root ! Start(path)
  }

  start()
}
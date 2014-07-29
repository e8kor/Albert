package org.albert.main

import java.nio.file.Paths

import org.albert._
import org.albert.actor.RootExecutor

object Main extends App {

  import org.albert.Akka._

  val root = system actorOf RootExecutor()

  val path = Paths get args(0)

  def start() = {
    root ! Start(path)
  }

  start()
}
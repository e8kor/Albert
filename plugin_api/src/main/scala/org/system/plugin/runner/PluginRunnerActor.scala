package org.system.plugin.runner

import akka.actor.{ActorLogging, Actor}

// TODO even this actor interface isn't really important, its enough that 3rd party plugin implement actor interface
// TODO it can be reasonable just to check that 3rd part plugin extend akka.actor.Actor
// TODO after such checks will be implemented this trait should be deprecated and removed
// TODO if trait will stay it should bring additional effort, for example some meaningful interaction with parent
// ---------
// TODO Cucumber runner test to take a look at -- need to check
// TODO Fitness test to take a look at -- more about user acceptance testing
// TODO test definitions should cover two most important here things is sending messages and sql execution
trait PluginRunnerActor extends Actor with ActorLogging
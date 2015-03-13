package org.system.test.scalamock

/**
 * Created by evgeniikorniichuk on 02/03/15.
 */
object TestData {

  val intField = 1

  def getIntField = intField

}

trait TestBehaviour {

  def intDef:Int

  def add1ToInt = intDef + 1

  def addToInt(value:Int) = intDef + value

  val dummyField = 10L

}

class TestBehaviourImpl extends TestBehaviour {

  override def intDef: Int = 20

  val anotherDummyField = 5L

}

package org.system.test.types

/**
 * Created by evgeniikorniichuk on 18/03/15.
 */
class StructuralTypes {

}

trait Container[E] {
  self =>

  type FBound <: Container[E] { type FBound <: self.FBound }

  def apply(event: E): FBound

}

class FBoundStructuralType extends Container[Int] {

  override type FBound = FBoundStructuralType

  override def apply(event: Int): FBound = {
    new FBoundStructuralType
  }
}

trait Foo extends Product with Serializable {
  type Self <: Foo
  def bar: Self
}

case class X() extends Foo {

  override def bar: Self = ???
}

trait SelfType[A <: SelfType[A]] {
  self:A =>

  def inc(i:Int)
}
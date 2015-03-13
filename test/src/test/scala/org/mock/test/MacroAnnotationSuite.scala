package org.mock.test

/**
 * Created by evgeniikorniichuk on 13/03/15.
 */
class MacroAnnotationSuite  extends MacroAnnotationSpec {

  def instance = new  MacroAnnotatedClass

   describe("Macro Annotated class ") {

     describe("should have") {

       it("dummy method which returns dummy string") {
         assert("dummy" == instance.dummy)
       }

       it("hello method which returns hello string") {
         assert("hello" == instance.hello)
       }

     }
   }
}

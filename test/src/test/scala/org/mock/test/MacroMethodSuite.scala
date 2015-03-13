package org.mock.test

import org.system.macros.TestMacro

/**
 * Created by evgeniikorniichuk on 13/03/15.
 */
class MacroMethodSuite extends MacroMethodSpec {

  def instance = new TestMacro

  def extendedInstance = new TestExtendedMacro

  describe("TestMacro class ") {

    describe("should have") {
      it("macroMethod which returns macroMethod as String") {
        assert(instance.macroMethod() == "macroMethod")
      }

    }
  }

  describe("TestExtendedMacro class ") {

    describe("should have") {
      it("macroMethod which returns macroMethod as String") {
        assert(instance.macroMethod() == "macroMethod")
      }

      it("nonMacroMethod which returns nonMacroMethod as String") {
        assert(extendedInstance.nonMacroMethod() == "nonMacroMethod")
      }

    }
  }
}

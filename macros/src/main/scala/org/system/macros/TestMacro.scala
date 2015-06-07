package org.system.macros

import scala.reflect.macros.whitebox
import scala.language.experimental.macros

/**
 * Created by evgeniikorniichuk on 12/03/15.
 */
class TestMacro {
  def macroMethod():String = macro TestMacro.impl
}

object TestMacro {

  def impl(c: whitebox.Context)():c.Expr[String] = {
    import c.universe._

    c.Expr[String](q""" "macroMethod" """)
  }
}

package org.system.macros

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

/**
 * Created by evgeniikorniichuk on 12/03/15.
 */
class TestMacroAnnotation extends StaticAnnotation{

  def macroTransform(annottees: Any*) = macro TestMacroAnnotation.impl

}

object TestMacroAnnotation {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def modifiedClass1(classDecl: ClassDef, compDeclOpt: Option[ModuleDef]) = {
      val (className, fields, bases, body) = try {
        val q"case class $className(..$fields) extends ..$bases { ..$body }" = classDecl
        (className, fields, bases, body)
      } catch {
        case _: MatchError => c.abort(c.enclosingPosition, "Annotation is only supported on case class")
      }
      c.Expr[Any](q"case class $className(..$fields) extends ..$bases { ..$body }")
    }

    def modifiedClass2(className: TypeName) = {

      c.Expr[Any](q"""case class $className() { def hello() = "hello" }""")
    }

    annottees.map(_.tree) match {
      case  List(q" class $className() { $body }") => modifiedClass2(className)
      case _ => c.abort(c.enclosingPosition, "Invalid annottee")
    }

  }

}
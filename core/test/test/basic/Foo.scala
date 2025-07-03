package my_rule

import scala.meta._
import scalafix.v1._

class Foo extends SyntacticRule("Foo") {
  override def fix(implicit doc: SyntacticDocument): Patch = {
    Patch.empty
  }
}

class Bar extends SemanticRule("Bar") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    Patch.empty
  }
}

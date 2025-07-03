package scalafix_rule_resource_gen

import sbt.*
import sbt.Keys.*

object ScalafixRuleResourceGen extends AutoPlugin {

  override val projectSettings: Seq[Def.Setting[?]] = Def.settings(
    Compile / resourceGenerators += Def.task {
      val rules = (Compile / compile).value
        .asInstanceOf[sbt.internal.inc.Analysis]
        .apis
        .internal
        .collect {
          case (className, analyzed) if analyzed.api.classApi.structure.parents.collect {
                case p: xsbti.api.Projection =>
                  p.id
              }.exists(Set("SyntacticRule", "SemanticRule")) =>
            className
        }
        .toList
        .sorted
      val log = streams.value.log
      if (rules.isEmpty) {
        log.warn("not found scalafix rule")
      }
      val output = (Compile / resourceManaged).value / "META-INF" / "services" / "scalafix.v1.Rule"
      IO.writeLines(output, rules)
      Seq(output)
    }
  )

}

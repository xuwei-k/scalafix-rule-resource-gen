package scalafix_rule_resource_gen

import sbt.*
import sbt.Keys.*
import xsbti.api.DefinitionType

object ScalafixRuleResourceGen extends AutoPlugin {

  object autoImport {
    @transient
    val scalafixRuleResourceGenRuleNames = taskKey[Seq[String]]("")
  }

  import autoImport.*

  override val projectSettings: Seq[Def.Setting[?]] = Def.settings(
    scalafixRuleResourceGenRuleNames := {
      (Compile / compile).value
        .asInstanceOf[sbt.internal.inc.Analysis]
        .apis
        .internal
        .collect {
          case (className, analyzed)
              if analyzed.api.classApi.structure.parents.collect { case p: xsbti.api.Projection =>
                p.id
              }.exists(
                Set("SyntacticRule", "SemanticRule")
              ) && (analyzed.api.classApi.definitionType == DefinitionType.ClassDef) &&
                !analyzed.api.classApi.modifiers.isAbstract =>
            className
        }
        .toList
        .sorted
    },
    Compile / resourceGenerators += Def.task {
      val rules = scalafixRuleResourceGenRuleNames.value
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

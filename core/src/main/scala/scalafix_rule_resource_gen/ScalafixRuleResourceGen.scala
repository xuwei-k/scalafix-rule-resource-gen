package scalafix_rule_resource_gen

import java.net.URLClassLoader
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
      scala.util.Using.resource(
        new URLClassLoader(
          (ScalafixRuleResourceGenCompat.classpathToFiles(
            (Compile / dependencyClasspath).value
          ) :+ (Compile / classDirectory).value).map(_.toURI.toURL).toArray
        )
      ) { classLoader =>
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

              if (
                classLoader
                  .loadClass(className)
                  .getConstructors
                  .exists(c => (c.getParameterCount == 0) && java.lang.reflect.Modifier.isPublic(c.getModifiers))
              ) {
                Some(className)
              } else {
                streams.value.log.warn(s"${className} does not have no-args public constructor")
                None
              }
          }
          .toList
          .flatten
          .sorted
      }
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

package scalafix_rule_resource_gen

import java.io.File
import sbt.Def.Classpath

private[scalafix_rule_resource_gen] object ScalafixRuleResourceGenCompat {
  def classpathToFiles(values: Classpath): Seq[File] =
    values.map(_.data)
}

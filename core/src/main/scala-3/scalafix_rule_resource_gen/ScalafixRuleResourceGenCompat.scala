package scalafix_rule_resource_gen

import java.io.File
import sbt.Def.Classpath
import sbt.Keys.fileConverter

private[scalafix_rule_resource_gen] object ScalafixRuleResourceGenCompat {
  inline def classpathToFiles(values: Classpath): Seq[File] = {
    val converter = fileConverter.value
    values.map(_.data).map(converter.toPath).map(_.toFile)
  }
}

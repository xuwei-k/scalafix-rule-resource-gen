# scalafix-rule-resource-gen: generate `META-INF/services/scalafix.v1.Rule`

`project/plugins.sbt`

```scala
addSbtPlugin("com.github.xuwei-k" % "scalafix-rule-resource-gen" % version)
```

`build.sbt`

```scala
val myRuleProject = project.enablePlugins(ScalafixRuleResourceGen)
```

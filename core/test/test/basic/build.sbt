libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % sys.props("scalafix.version")

scalaVersion := "2.13.16"

enablePlugins(ScalafixRuleResourceGen)

TaskKey[Unit]("check") := {
  val jar = (Compile / packageBin).value
  IO.withTemporaryDirectory { tmp =>
    IO.unzip(jar, tmp)
    val lines = IO.readLines(tmp / "META-INF/services/scalafix.v1.Rule")
    assert(lines == Seq("my_rule.Bar", "my_rule.Foo"))
  }
}

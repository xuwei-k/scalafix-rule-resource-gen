import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations.*

val commonSettings = Def.settings(
  organization := "com.github.xuwei-k",
  publishTo := (if (isSnapshot.value) None else localStaging.value),
  scalacOptions ++= {
    scalaBinaryVersion.value match {
      case "2.12" =>
        Seq(
          "-language:higherKinds",
          "-Xsource:3",
        )
      case _ =>
        Nil
    }
  },
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
  ),
  pomExtra := (
    <developers>
    <developer>
      <id>xuwei-k</id>
      <name>Kenji Yoshida</name>
      <url>https://github.com/xuwei-k</url>
    </developer>
  </developers>
  <scm>
    <url>git@github.com:xuwei-k/scalafix-rule-resource-gen.git</url>
    <connection>scm:git:git@github.com:xuwei-k/scalafix-rule-resource-gen.git</connection>
  </scm>
  ),
  description := "generate META-INF/services/scalafix.v1.Rule",
  organization := "com.github.xuwei-k",
  homepage := Some(url("https://github.com/xuwei-k/scalafix-rule-resource-gen")),
  licenses := List(
    "MIT License" -> url("https://opensource.org/licenses/mit-license")
  ),
)

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommandAndRemaining("publishSigned"),
  releaseStepCommandAndRemaining("sonaRelease"),
  setNextVersion,
  commitNextVersion,
  pushChanges
)

commonSettings

publish / skip := true

val `scalafix-rule-resource-gen` = projectMatrix
  .in(file("core"))
  .enablePlugins(SbtPlugin)
  .jvmPlatform(
    Seq("2.12.20", "3.7.2")
  )
  .settings(
    commonSettings,
    sbtTestDirectory := sourceDirectory.value.getParentFile / "test",
    scriptedLaunchOpts ++= Seq[(String, String)](
      "plugin.version" -> version.value,
      "scalafix.version" -> _root_.scalafix.sbt.BuildInfo.scalafixVersion
    ).map { case (k, v) =>
      s"-D${k}=${v}"
    },
    scriptedBufferLog := false,
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" =>
          sbtVersion.value
        case _ =>
          "2.0.0-RC2"
      }
    },
  )

ThisBuild / scalafixDependencies += "com.github.xuwei-k" %% "scalafix-rules" % "0.6.13"

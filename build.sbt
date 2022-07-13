import com.typesafe.tools.mima.core._
import sbtcrossproject.crossProject

addCommandAlias("fmt", "; scalafmtAll; scalafmtSbt")
addCommandAlias("fmtCheck", "; scalafmtCheckAll; scalafmtSbtCheck")

ThisBuild / baseVersion := "1.1"

ThisBuild / organization := "org.scodec"
ThisBuild / organizationName := "Scodec"

ThisBuild / homepage := Some(url("https://github.com/scodec/scodec-cats"))
ThisBuild / startYear := Some(2013)

ThisBuild / crossScalaVersions := Seq("2.12.13", "2.13.5", "3.0.0")

ThisBuild / strictSemVer := false

ThisBuild / githubWorkflowJavaVersions := Seq("adopt@1.8")

ThisBuild / githubWorkflowBuild := Seq(
  WorkflowStep.Sbt(List("fmtCheck", "test", "+mimaReportBinaryIssues"))
)

ThisBuild / spiewakCiReleaseSnapshots := true

ThisBuild / spiewakMainBranches := List("main")

ThisBuild / scmInfo := Some(
  ScmInfo(url("https://github.com/scodec/scodec-cats"), "git@github.com:scodec/scodec-cats.git")
)

ThisBuild / licenses := List(
  ("BSD-3-Clause", url("https://github.com/scodec/scodec-cats/blob/main/LICENSE"))
)

ThisBuild / testFrameworks += new TestFramework("munit.Framework")

ThisBuild / publishGithubUser := "mpilquist"
ThisBuild / publishFullName := "Michael Pilquist"
ThisBuild / developers ++= List(
  "durban" -> "Daniel Urban"
).map { case (username, fullName) =>
  Developer(username, fullName, s"@$username", url(s"https://github.com/$username"))
}

ThisBuild / fatalWarningsInCI := false

ThisBuild / mimaBinaryIssueFilters ++= Seq(
)

lazy val root =
  project
    .in(file("."))
    .aggregate(coreJVM, coreJS)
    .enablePlugins(NoPublishPlugin, SonatypeCiReleasePlugin)

lazy val core = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .enablePlugins(SbtOsgi)
  .settings(name := "scodec-cats")
  .settings(dottyLibrarySettings)
  .settings(dottyJsSettings(ThisBuild / crossScalaVersions))
  .settings(
    libraryDependencies ++= Seq(
      "org.scodec" %%% "scodec-bits" % "1.1.34",
      "org.scodec" %%% "scodec-core" % (if (isDotty.value) "2.1.0" else "1.11.7"),
      "org.typelevel" %%% "cats-core" % "2.8.0",
      "org.typelevel" %%% "cats-laws" % "2.8.0" % Test,
      "org.typelevel" %%% "discipline-munit" % "1.0.9" % Test
    )
  )
  .jvmSettings(osgiSettings)
  .jvmSettings(
    OsgiKeys.exportPackage := Seq("scodec.interop.cats.*;version=${Bundle-Version}"),
    OsgiKeys.importPackage := Seq(
      """scodec.*;version="$<range;[==,=+);$<@>>"""",
      """scala.*;version="$<range;[==,=+);$<@>>"""",
      """cats.*;version="$<range;[==,=+);$<@>>"""",
      "*"
    )
  )

lazy val coreJVM = core.jvm
lazy val coreJS =
  core.js.settings(
    scalaJSLinkerConfig ~= (_.withModuleKind(ModuleKind.CommonJSModule))
  )

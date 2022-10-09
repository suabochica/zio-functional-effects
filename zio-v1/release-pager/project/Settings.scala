import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys.{ scalacOptions, _ }
import sbt._
import sbt.util.Level
import wartremover.WartRemover.autoImport.wartremoverErrors
import wartremover.{ Wart, Warts }

import Dependencies._

object Settings {
  val warts = Warts.allBut(
    Wart.Any,
    Wart.DefaultArguments,
    Wart.JavaSerializable,
    Wart.Nothing,
    Wart.Overloading,
    Wart.PublicInference,
    Wart.Serializable,
    Wart.StringPlusAny,
    Wart.TraversableOps
  )

  val commonSettings =
    Seq(
      scalaVersion         := "2.13.6",
      scalacOptions        := Seq(
        "-deprecation",
        "-encoding",
        "utf-8",
        "-explaintypes",
        "-feature",
        "-language:postfixOps",
        "-language:higherKinds",
        "-language:implicitConversions",
        "-unchecked",
        "-Ymacro-annotations",
        "-Xcheckinit",
        "-Xfatal-warnings"
      ),
      logLevel             := Level.Info,
      version              := (version in ThisBuild).value,
      scalafmtOnCompile    := true,
      wartremoverErrors in (Compile, compile) ++= warts,
      wartremoverErrors in (Test, compile) ++= warts,
      testFrameworks       := Seq(new TestFramework("zio.test.sbt.ZTestFramework")),
      cancelable in Global := true,
      fork in Global       := true, // https://github.com/sbt/sbt/issues/2274
      resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    )

  val storageDependencies = List(zio, zioCats) ++ doobie
  val serviceDependencies = List(
    canoe,
    fs2Core,
    slf4j,
    zioCats,
    zioMacros,
    zioTest,
    zioTestSbt
  ) ++ circe
  val serverDependencies  = List(flyway, pureconfig, h2)
}

ThisBuild / scalaVersion := "2.13.4"

val zioVersion = "1.0.4-2"

lazy val root = (project in file("."))
  .settings(
    name := "zionomicon",
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio" % zioVersion,
      "dev.zio" %% "zio-test" % zioVersion,
      "dev.zio" %% "zio-test-sbt" % zioVersion
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

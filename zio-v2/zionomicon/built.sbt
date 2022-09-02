val ZIOVersion  = "2.0.0-M1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "zionomicon-exercises",
    organization := "com.sua",
    scalaVersion := "3.0.1"
  )

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")
addCommandAlias(
  "check",
  "all scalafmtSbtCheck scalafmtCheck test:scalafmtCheck"
)

libraryDependencies ++= { Seq(
    "dev.zio"                    %% "zio"                 % ZIOVersion,
    "dev.zio"                    %% "zio-streams"         % ZIOVersion,
    "dev.zio"                    %% "zio-interop-cats"    % "3.1.1.0",
    "dev.zio"                    %% "zio-test"            % ZIOVersion % Test,
    "dev.zio"                    %% "zio-test-sbt"        % ZIOVersion % Test
  )
}

testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

scalacOptions ++= List(
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings", // Fail on warnings, not just errors
  // "-Vimplicits",
  // "-Vtype-diffs",
  "-source:future",   // Choices: future and future-migration. I use this to force future deprecation warnings, etc.
  "-new-syntax",      // Require `then` and `do` in control expressions.
  "-language:strictEquality" // multiversal equality
)

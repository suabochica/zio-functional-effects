val ZIOVersion = "1.0.4-2"

libraryDependencies ++= Seq(
  // ZIO
  "dev.zio" %% "zio"          % ZIOVersion,
  "dev.zio" %% "zio-streams"  % ZIOVersion,
  "dev.zio" %% "zio-test"     % ZIOVersion % "test",
  "dev.zio" %% "zio-test-sbt" % ZIOVersion % "test",
  // URL parsing
  "io.lemonlabs" %% "scala-uri" % "4.0.2"
)


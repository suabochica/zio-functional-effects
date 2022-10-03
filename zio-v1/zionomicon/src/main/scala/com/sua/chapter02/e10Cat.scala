package com.sua.chapter02

import zio.console.Console
import zio.{App, ExitCode, URIO, ZIO}

/** Using the following code as a foundation, write a ZIO application that
  * prints out the contents of whatever files are passed into the program as
  * command-line arguments. You should use the function `readFileZio` that you
  * developed in these exercises, as well as `ZIO.foreach`.
  */
object e10Cat {
  import e01ReadFile.readFileZio
  import e05ForComprehension.printLine

  object Cat extends App {

    def run(
        commandLineArguments: List[String],
    ): URIO[Any with Console, ExitCode] =
      cat(commandLineArguments).exitCode

    def cat(
        commandLineArguments: List[String],
    ): ZIO[Any, Throwable, List[Unit]] =
      ZIO.foreach(commandLineArguments)(file =>
        readFileZio(file).flatMap(printLine),
      )
  }
}

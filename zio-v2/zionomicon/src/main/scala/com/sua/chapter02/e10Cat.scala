package com.sua.chapter02

import zio.{Chunk, URIO, ZIO, ZIOAppDefault}

/**
 * Using the following code as a foundation, write a ZIO application that
 * prints out the contents of whatever files are passed into the program as
 * command-line arguments. You should use the function 'readFileZIO' that you
 * developed in these exercises, as well a 'ZIO.forEach'
 */

object e10Cat {
  import e01ReadFile.readFileZIO
  import e05ForComprehension.printLine

  object cat extends ZIOAppDefault {

    val run =
      for {
        args <- ZIOAppArgs.getArgs
        _ <- cat(args)
      } yield Unit

    def cat(files: Chunk[String]) =
      ZIO.foreach(files) { file =>
        readFileZIO(file).flatMap(printLine())
      }
  }
}

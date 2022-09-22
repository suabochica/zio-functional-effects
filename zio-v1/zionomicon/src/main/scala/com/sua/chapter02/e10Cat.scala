package com.sua.chapter02

import zio.{Chunk, ZIO, ZIOAppArgs, ZIOAppDefault}

/** Using the following code as a foundation, write a ZIO application that
  * prints out the contents of whatever files are passed into the program as
  * command-line arguments. You should use the function `readFileZio` that you
  * developed in these exercises, as well as `ZIO.foreach`.
  */
object e10Cat {
  import java.io.IOException
  import e01ReadFile.readFileZio
  import e05ForComprehension.printLine

  // TODO: Fix error on ZIOAppDefault
  object Cat extends ZIOAppDefault {

    val run =
      for {
        args <- ZIOAppArgs.getArgs // TODO: Fix error on ZIOAppArgs
        _ <- cat(args)
      } yield Unit

    def cat(files: Chunk[String]): ZIO[Any, IOException, Unit] =
      ZIO.foreach(files)(file => readFileZio(file).flatMap(printLine))
  }
}

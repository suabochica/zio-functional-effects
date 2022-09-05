package com.sua.chapter02

import zio.ZIO

/**
 * Using the flatMap method of ZIO effects, together with the readFileZio
 *  and writeFileZio functions that you wrote, implement a ZIO version of
 *  the function copyFile.
 */

object e03 {
  import e01Files.{readFile, readFileZio}
  import e02Files.{writeFile, writeFileZio}

  def copyFile(source: String, destiny: String): Unit = {
    val contents = readFile(source)

    writeFile(destiny, source)
  }

  def copyFileZio(source: String, destiny: String): ZIO[Any, Throwable, Unit] =
    readFileZio(source).flatMap(text => writeFileZio(destiny, text))
}
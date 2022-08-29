package com.sua.chapter02

import scala.io.Source
import zio.ZIO

/**
 * Implement a ZIO version of the function `readFile` by using the
 * `ZIO.effect` constructor
 */

object e01_Files {
  def readFile(filePath: String): String = {
    val source = Source.fromFile(filePath)

    try source.getLines.mkString
    finally source.close()
  }

  def readFileZio(file: String): ZIO[Any, Throwable, String] =
    ZIO.effect(readFile(file))
}

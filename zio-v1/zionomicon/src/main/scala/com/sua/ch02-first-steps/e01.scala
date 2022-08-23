package com.sua.ch02-first-steps

import zio.ZIO

/**
 * Implement a ZIO version of the function `readFile` by using the
 * `ZIO.effect` constructor
 */

object e01 {
  def readFile(file: String): String = {
    val source = scala.io.Source.fromFile(file)

    try source.getLines.mkString
    finally source.close()
  }

  def readFileZio(file: String): ZIO[Any, Throwable, String] =
    ZIO.effect(readFile(file))
}


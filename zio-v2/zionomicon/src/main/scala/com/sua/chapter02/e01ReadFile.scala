package com.sua.chapter02

import scala.io.Source
import zio.*

/**
 * Implement a ZIO version of the function `readFile` by using the
 * `ZIO.attempt` constructor.
 */

object e01ReadFile {
  def readFile(filePath: String): String = {
    val source = Source.fromFile(filePath)

    try source.getLines.mkString("\n")
    finally source.close()
  }

  def readFileZIO(filePath: String): ZIO[Any, Throwable, String] = {
    ZIO.attempt(readFile(filePath))
  }
}


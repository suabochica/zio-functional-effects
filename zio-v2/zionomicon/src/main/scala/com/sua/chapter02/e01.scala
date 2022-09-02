package com.sua.chapter02

import scala.io.Source
import zio.*

object e01Files {
  def readFile(filePath: String): String {
    val source = Source.fromFile(filePath)

    try source.getLines.mkString("\n")
    finally source.close()
  }

  def readFileZio(filePath: String): ZIO[Any, Throwable, String] {
    ZIO.effect(readFile(filePath))
  }
}

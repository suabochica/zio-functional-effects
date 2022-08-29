package com.sua.chapter02

import java.io{File, PrintWriter}
import zio.ZIO

/**
 * Implement a ZIO versio of the function writheFile by using ZIO.effect constructor
 */

object e02_Files {
  def writeFile(filePath: String, text: String): Unit = {
    val printWriter = new PrintWriter(File(filePath))

    try printWriter.write(text)
    finally printWriter.close
  }

  def writeFileZio(file: String, text: String): ZIO[Any, Throwable, Unit] =
    ZIO.effect(writeFile(file, text))
}
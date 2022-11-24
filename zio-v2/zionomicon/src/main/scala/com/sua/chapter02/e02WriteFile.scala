package com.sua.chapter02

import java.io.File
import java.io.PrintWriter
import zio.{Task, ZIO}

/**
 * Implement a ZIO version of the function `writeFile` by using the
 * `ZIO.attempt` constructor
 */
object e02WriteFile {
  def writeFile(filePath: String, text: String): Unit = {
    val printWriter = new PrintWriter(new File(filePath))

    try printWriter.write(text)
    finally printWriter.close()
  }

  def writeFileZIO(filePath: String, text: String): Task[Unit] = {
    ZIO.attempt(writeFile(filePath, text))
  }
}
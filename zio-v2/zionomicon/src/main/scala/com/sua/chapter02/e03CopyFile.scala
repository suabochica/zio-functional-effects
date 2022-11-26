package com.sua.chapter02

import zio.ZIO

/**
 * Using the `flatMap` method of ZIO effects, together with the `readFileZIO`
 * and the `writeFileZIO` function that you wrote, implement a ZIO version of
 * the function `copyFile`
 */

object e03CopyFile {
  import e01ReadFile.{readFile, readFileZIO}
  import e02WriteFile.{writeFile, writeFileZIO}

  def copyFile(source: String, dest: String): Unit = {
    val contents = readFile(source)

    writeFile(dest, contents)
  }

  def copyFileZIO(source: String, dest: String): Unit = {
    readFileZIO(source).flatMap(contents => writeFileZIO(dest, contents))
  }
}

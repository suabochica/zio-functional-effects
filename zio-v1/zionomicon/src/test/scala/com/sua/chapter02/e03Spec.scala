package com .sua.chapter02

import e03Files.{copyFile}

import zio.test.{assert,DefaultRunnableSpec}
import zio.test.Assertion.equalTo

object e03FilesSpec extends DefaultRunnableSpec{
  val testSourcePath: String = getClass.getResource("/testFile.txt").getPath
  val testDestinyPath: String = "./testDestinyPath"

  def spec = {
    suite("FileSpecs - Exercise 03")(
      test("copy file succeeds") {
        val result: Unit = copyFile(testSourcePath, testDestinyPath)

        assert(result)(equalTo())
      }
    )
  }
}
package com.sua.chapter02

import e02WriteFile.{writeFile, writeFileZio}
import zio.test.{assert, assertM, testM, DefaultRunnableSpec}
import zio.test.Assertion.{equalTo, isUnit, succeeds}

object e02WriteFileSpec extends DefaultRunnableSpec {
  val testFileTxt: String  = "write\nfile"
  val testFilePath: String = getClass.getResource("/testFile.txt").getPath
  val testWriteFilePath: String =
    testFilePath.replaceFirst("testFilePath.txt", "testWriteFilePath.txt")

  def spec = {
    suite("File Specs - Exercise 02")(
      test("write file succeeds") {
        val result = writeFile(testWriteFilePath, testFileTxt)

        assert(result)(equalTo())
      },
    )
  }
}

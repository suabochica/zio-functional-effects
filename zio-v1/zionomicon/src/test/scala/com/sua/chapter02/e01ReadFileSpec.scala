package com.sua.chapter02

import e01ReadFile.{readFile, readFileZio}
import zio.test.{assert, assertM, testM, DefaultRunnableSpec}
import zio.test.Assertion.equalTo

object e01ReadFileSpec extends DefaultRunnableSpec {
  val testFileTxt: String  = "hi\nbye"
  val testFilePath: String = getClass.getResource("/testFile.txt").getPath
  def spec = {
    suite("File Specs - Exercise 01")(
      test("read file succeeds") {
        val result = readFile(testFilePath)

        assert(result)(equalTo(testFileTxt))
      },
      testM("read file in ZIO effect") {
        for {
          result <- readFileZio(testFilePath)
        } yield assert(result)(equalTo(testFileTxt))
      },
    )
  }
}

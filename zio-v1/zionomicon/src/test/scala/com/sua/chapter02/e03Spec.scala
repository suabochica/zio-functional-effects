package com .sua.chapter02

import zio.test.{assert,DefaultRunnableSpec}
import zio.test.Assertion.equalTo

object e03FileSpec extends DefaultRunnableSpec{
  val testSourcePath: String = getClass.getResource("/testFile.txt").getPath
  val testDestPath: String = "./testSourcePath"

  def spec = ("FileSpecs - Exercise 03")(
    test("copy file succeds") {
      val result = copyFile(testSourcePath, testDestPath)

      assert(result)(equalTo())
    }
  )
}
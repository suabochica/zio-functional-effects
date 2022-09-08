package com.sua.chapter02

import zio.test.Assertion.{contains, equalTo}
import zio.test.{DefaultRunnableSpec, assert}

object e04FilesSpec extends DefaultRunnableSpec {

  def spec = {
    suite("FileSpecs - Exercise 04")(
      test("Use for comprehensions") {
        val name = "Jim"
        val result = s"Hello, ${name}"

        assert(result)(equalTo("Hello, Jim"))
      }
    )
  }
}
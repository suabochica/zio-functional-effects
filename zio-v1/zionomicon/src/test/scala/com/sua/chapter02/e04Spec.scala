package com.sua.chapter02

import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, assert}

object e04ForComprehensionSpec extends DefaultRunnableSpec {

  def spec = {
    suite("For Comprehension - Exercise 04")(
      test("Use for comprehensions") {
        val name = "Jim"
        val result = s"Hello, ${name}"

        assert(result)(equalTo("Hello, Jim"))
      }
    )
  }
}
package com.sua.chapter02

import zio.console

import zio.test.Assertion.equalTo
import zio.test.{assert, DefaultRunnableSpec}
import zio.test.environment.TestConsole

// TODO: Fix type mismatch
object e05ForComprehensionSpec extends DefaultRunnableSpec {

  def spec = {
    suite("For Comprehension - Exercise 05")(
      test("Use for comprehensions print line") {
        val expected = Vector("hello\n", "goodbye\n")

        for {
          _      <- console.putStrLn("hello")
          _      <- console.putStrLn("goodbye")
          output <- TestConsole.output
        } yield assert(output)(equalTo(expected))
      },
    )
  }
}

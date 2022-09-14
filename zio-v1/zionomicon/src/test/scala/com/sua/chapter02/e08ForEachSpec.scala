package com.sua.chapter02

import zio.test.Assertion.equalTo
import zio.test.{assert, DefaultRunnableSpec}

object e08ForEachSpec extends DefaultRunnableSpec {
  def spec = {
    suite("For Each - Exercise 08")(
      test(
        "should return an effect that sequentially runs the specified function on every element of the array",
      ) {
        val expected = ???

        for {
          _ <- ???
        } yield assert(forEach)(equalTo(expected))
      },
    )
  }
}

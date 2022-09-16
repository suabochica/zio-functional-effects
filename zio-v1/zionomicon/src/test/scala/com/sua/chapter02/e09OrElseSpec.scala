package com.sua.chapter02

import zio.test.Assertion.equalTo
import zio.test.{assert, DefaultRunnableSpec}

object e09OrElseSpec extends DefaultRunnableSpec {
  def spec = {
    suite("Or Else - Exercise 09")(
      test(
        "should return an effect that tries the left hand side, but if that effect fails, it will fallback to the effect on the right hand side.",
      ) {
        val expected = ???

        for {
          _ <- ???
        } yield assert(orElse)(equalTo(expected))
      },
    )
  }
}

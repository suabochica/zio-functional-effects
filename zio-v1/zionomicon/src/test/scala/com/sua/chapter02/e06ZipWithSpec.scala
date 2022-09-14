package com.sua.chapter02

import zio.test.Assertion.equalTo
import zio.test.{assert, DefaultRunnableSpec}

object e06ZipWithSpec extends DefaultRunnableSpec {
  def spec = {
    suite("Zip With - Exercise 06")(
      test("should return an effect that sequentially composes two effects") {
        val expected = ???

        for {
          _ <- ???
        } yield assert(zipUnit)(equalTo(expected)
      },
    )
  }
}

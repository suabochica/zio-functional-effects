package com.sua.chapter02

import zio.test.Assertion.equalTo
import zio.test.{assert, DefaultRunnableSpec}

object e07CollectAll extends DefaultRunnableSpec {
  def spec = {
    suite("Collect All - Exercise 07")(
      test(
        "should return an effect that sequentially collects the results of specified effects",
      ) {
        val expected = ???

        for {
          _ <- ???
        } yield assert(collectAll)(equalTo(expected))
      },
    )
  }
}

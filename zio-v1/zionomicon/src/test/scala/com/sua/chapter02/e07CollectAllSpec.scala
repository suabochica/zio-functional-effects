package com.sua.chapter02

import zio.ZIO
import zio.IO
import zio.test.Assertion.equalTo
import zio.test.{assert, DefaultRunnableSpec}

object e07CollectAllSpec extends DefaultRunnableSpec {
  def spec = {
    suite("Collect All - Exercise 07")(
      test(
        "should return an effect that sequentially collects the results of specified effects",
      ) {
        val expected = Right(List((), ()))
        // TODO: Check how to mock a IO effect with a collectAll method
        val testIO = IO.succeed()

        for {
          _ <- ???
        } yield assert(collectAll)(equalTo(expected))
      },
    )
  }
}

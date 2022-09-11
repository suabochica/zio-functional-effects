package com.sua.chapter02


import zio.test.Assertion.equalTo
import zio.test.{DefaultRunnableSpec, assert, assertM}


object e06ZipWith extends DefaultRunnableSpec {
  def spec = {
    suite("Zip With - Exercise 06")(
      test("should return an effect that sequentially composes two effects") {
        for {
          _ <- ???
        } yield assert(zipUnit)(equalTo(Right("()()")))
      }
    )
  }
}


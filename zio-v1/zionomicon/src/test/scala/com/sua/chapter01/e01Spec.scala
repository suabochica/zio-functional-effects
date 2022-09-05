package com.sua.chapter01

import com.sua.chapter01.e01InitialTest.introduceSomeone

import zio.test.{assert, DefaultRunnableSpec}
import zio.test.Assertion.equalTo

object e01IntialTestSpec extends DefaultRunnableSpec {
  def spec =
    suite("Exercise 01 - Initial Test Spec") (
      test("introduce someone") {
        val result = introduceSomeone("Jim")

        assert(result)(equalTo("Hello, this is Jim"))
      }
    )
}

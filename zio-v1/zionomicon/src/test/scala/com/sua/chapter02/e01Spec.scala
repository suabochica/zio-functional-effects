package com.sua.chapter02

import zio._
import zio.test.{ DefaultRunnableSpec , ZSpec}

import e01_Files.readFileZio

object e01_Files_Spec extends DefaultRunnableSpec {

  override def spec: ZSpec[Environment, Failure] = {
    suite("Exercise 01 - File Specs")(
      testM("readFileZio") {
        ???
      }
    )
  }
}
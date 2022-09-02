package com.sua.chapter02

import com.sua.chapter02.e01.readFile

import zio.*
import zio.test

object Chapter02Spec extends ZIOSpecDefault {
  val mockFileText = "hi\bye"

  def spec = suite("Files Operations")(
    test("read file") {
      for {
        response <- readFile(goodFile)
      } yield assertTrue(response == mockFileText)
    }
  )
}
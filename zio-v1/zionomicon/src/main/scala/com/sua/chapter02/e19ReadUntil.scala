package com.sua.chapter02

import zio.ZIO
import zio.console.{getStrLn, Console}

import java.io.IOException

/** Using the `Console` service and recursion, write a function that will
  * repeatedly read input from the console until the specified user-defined
  * function evaluates to `true` on the input.
  */
object e19ReadUntil {

  def readUntil(
      acceptInput: String => Boolean,
  ): ZIO[Console, IOException, String] =
    getStrLn.flatMap { input =>
      if (acceptInput(input)) ZIO.succeed(input)
      else readUntil(acceptInput)
    }
}

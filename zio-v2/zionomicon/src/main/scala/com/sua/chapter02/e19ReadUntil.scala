package com.sua.chapter02

import zio.{Console, ZIO}
import java.io.IOException
/**
 * Using the `Console` service and recursion, write a function that will
 * repeatedly read input from the console until the specified used-defined
 * function evaluates to `true` on the input.
 */
object e19ReadUntil {
  def readUntil(
               acceptInput: String => Boolean
               ): ZIO[Console, IOException, String] =
    Console.readLine.flatMap { input =>
      if (acceptInput(input)) ZIO.succeed(input)
      else readUntil(acceptInput)
    }
}
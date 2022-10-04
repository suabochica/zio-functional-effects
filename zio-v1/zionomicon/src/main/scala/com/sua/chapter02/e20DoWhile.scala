package com.sua.chapter02

import zio.ZIO

/** Using recursion, write a function that will continue evaluating the
  * specified effect, until the specified user-defined function evaluates to
  * `true` on the output of the effect.
  */
object e20DoWhile {
  def doWhile[R, E, A](
      body: ZIO[R, E, A],
  )(condition: A => Boolean): ZIO[R, E, A] =
    body.flatMap { success =>
      if (condition(success)) ZIO.succeed(success)
      else doWhile(body)(condition)
    }
}

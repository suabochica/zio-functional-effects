package com.sua.chapter04

import zio.{Cause, ZIO}

 /**
  * Using the `ZIO#foldCauseM` method, implement the following function.
  */

object e10FoldCause {
  def catchAllCause[R, E1, E2, A](
    zio: ZIO[R, E1, A],
    handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] =
    zio.foldCauseM(cause => handler(cause), a => ZIO.succeed(a))
}

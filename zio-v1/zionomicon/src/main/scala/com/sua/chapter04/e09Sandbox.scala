package com.sua.chapter04

import zio.{Cause, ZIO}

 /**
  * Using the `ZIO#sandbox` method, implement the following function.
  */

object e09Sandbox {
  def catchAllCause[R, E1, E2, A](
      zio: ZIO[R, E1, A],
      handler: Cause[E1] => ZIO[R, E2, A]
  ): ZIO[R, E2, A] =
    zio.sandbox.foldM(cause => handler(cause), a => ZIO.succeed(a))
}

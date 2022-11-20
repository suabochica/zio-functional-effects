package com.sua.chapter04

import zio.ZIO

/**
   * Using the `ZIO#foldCauseZIO` operator and the `Cause#defects` method,
   * implement the following function. This function should take the effect,
   * inspect defects, and if a suitable defect is found, it should recover from
   * the error with the help of the specified function, which generates a new
   * success value for such a defect.
   */
  object e02RecoverFromSomeDefects {

    def recoverFromSomeDefects[R, E, A](zio: ZIO[R, E, A])(
      f: Throwable => Option[A]
    ): ZIO[R, E, A] =
      zio.foldCauseM(
        cause =>
          cause.defects
            .collectFirst(Function.unlift(f))
            .fold[ZIO[R, E, A]](ZIO.halt(cause))(a => ZIO.succeed(a)),
        a => ZIO.succeed(a)
      )
  }

package com.sua.chapter04

import zio.ZIO

/**
 * Using the `ZIO#foldM` method, implement the following two functions, which
 * make working with `Either` values easier, by shifting the unexpected case
 * into the error channel (and reversing this shifting).
 */

object e08RightUnright {
  def right[R, E, A, B](
    zio: ZIO[R, E, Either[A, B]]
  ): ZIO[R, Either[E, A], B] =
    zio.foldM(
      e => ZIO.fail(Left(e)),
      _.fold(a => ZIO.fail(Right(a)), b => ZIO.succeed(b))
    )

  def unright[R, E, A, B](
    zio: ZIO[R, Either[E, A], B]
  ): ZIO[R, E, Either[A, B]] =
  zio.foldM(
      _.fold(e => ZIO.fail(e), a => ZIO.left(a)),
      b => ZIO.right(b)
    )
}

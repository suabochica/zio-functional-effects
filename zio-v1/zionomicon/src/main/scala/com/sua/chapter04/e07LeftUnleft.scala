package com.sua.chapter04

import zio.ZIO

/**
 * Using the `ZIO#foldM` method, implement the following two functions, which
 * make working with `Either` values easier, by shifting the unexpected case
 * into the error channel (and reversing this shifting).
 */

object e07LeftUnleft {
  def left[R, E, A, B](
      zio: ZIO[R, E, Either[A, B]]
  ): ZIO[R, Either[E, B], A] =
    zio.foldM(
      e => ZIO.fail(Left(e)),
      _.fold(a => ZIO.succeed(a), b => ZIO.fail(Right(b)))
    )

  def unleft[R, E, A, B](
      zio: ZIO[R, Either[E, B], A]
  ): ZIO[R, E, Either[A, B]] =
    zio.foldM(
      _.fold(e => ZIO.fail(e), b => ZIO.right(b)),
      a => ZIO.left(a)
    )
}
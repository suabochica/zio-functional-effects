package com.sua.chapter02

import zio.ZIO

/**
 *  Using 'ZIO.fail' and 'ZIO.succeed', implement the following function
 *  which converts and 'Either' into a ZIO effect.
 */

object e11EitherToZIO {
  def eitherToZIO[E, A](either: Either[E, A]): ZIO[Any, E, A] =
    either.fold(error => ZIO.fail(error), success => ZIO.succeed(success))
}

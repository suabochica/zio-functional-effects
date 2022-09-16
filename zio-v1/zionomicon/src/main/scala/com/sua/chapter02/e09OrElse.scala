package com.sua.chapter02

import zio.ZIO

/** Implement the 'orElse' function in terms of the toy model of a ZIO effect.
  * The function should return an effect that tries the left hand side, but if
  * that effect fails, it will fallback to the effect on the right hand side.
  */

object e09OrElse {
  import e06ZipWith.ToyZIO
  def orElse[R, E1, E2, A](
      self: ToyZIO[R, E1, A],
      that: ToyZIO[R, E2, A],
  ): ToyZIO[R, E2, A] =
    ToyZIO { environment =>
      self.run(environment) match {
        case Left(e1) => that.run(environment)
        case Right(a) => Right(a)
      }
    }
}

package com.sua.chapter02

import zio.{Task, ZIO}

/**
 * Implement the 'zipWith' function in terms of a ZIO effect
 * toy's model. The function should return an effect that sequentially
 * compose specified effects, merging their result with the
 * a user effect defined
 */

object e06ZipWith {
  final case class ToyZIO[-R, +E, +A](run: R => Either[E, A])

  def zipWith[R, E, A, B, C](
      self: ToyZIO[R, E, A],
      that: ToyZIO[R, E, B]
  )(merge: (A, B) => C): ToyZIO[R, E, C] =
    ToyZIO { environment =>
      for {
        a <- self.run(environment)
        b <- that.run(environment)
      } yield merge(a, b)
    }
}
package com.sua.chapter02

/**
 * Implement the `zipWith` function in terms of the toy model of a ZIO
 * effect. The function should return an effect that sequentially composes
 * the specified effects, merging their result with the specified
 * user-defined function.
 */

object e06ZipWith {
  final case class ToyZIO[-R, +E, +A](run: R => Either[E, A])

  def zipWith[R, E, A, B, C](
    self: ToyZIO[R, E, A],
    that: ToyZIO[R, E ,B]
  )(f: (A, B) => C): ToyZIO[R, E, C] =
    ToyZIO(environment =>
      self.run(environment).
        flatMap(a => that.run(environment)
          .map(b => f(a, b))
        )
    )
}
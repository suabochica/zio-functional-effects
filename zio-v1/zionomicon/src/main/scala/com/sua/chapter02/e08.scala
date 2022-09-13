package com.sua.chapter02

/** Implement the 'foreach' function in terms of a ZIO effect toy's model. The
  * function should return an effect that sequentially runs the specified
  * function on every element of the array
  */

object e08ForEach {
  final case class ToyZIO[-R, +E, +A](run: R => Either[E, A])

  def foreach[R, E, A, B](
      input: Iterable[A],
  )(f: A => ToyZIO[R, E, B]): ToyZIO[R, E, List[B]] = {

    def foreachR(
        input: Iterable[A],
        accumulator: Either[E, List[B]],
        environment: R,
    ): Either[E, List[B]] =
      ???
  }
}

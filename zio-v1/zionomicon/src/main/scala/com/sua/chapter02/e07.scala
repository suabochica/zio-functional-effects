package com.sua.chapter02

/** Implement the 'collectAll' function in terms of a ZIO effect toy's model.
  * The function should return an effect that sequentially collects the results
  * of the specified collection effects
  */

object e07CollectAll {
  final case class ToyZIO[-R, +E, +A](run: R => Either[E, A])

  def collect[R, E, A](
      input: Iterable[ToyZIO[R, E, A]],
  ): ToyZIO[R, E, List[A]] =
    ???

  def collectAll[R, E, A](
      input: Iterable[ToyZIO[R, E, A]],
  ): ToyZIO[R, E, List[A]] =
    ???
}

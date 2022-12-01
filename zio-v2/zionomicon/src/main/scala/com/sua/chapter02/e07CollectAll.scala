package com.sua.chapter02

/**
 * Implement the `collectAll` function in terms of the toy model of a ZIO
 * effect. The function should return an effect that sequentially collects
 * the results of the specified collection of effects.
 */
object e07CollectAll {
  import e06ZipWith.{zipWith, ToyZIO}

  def succeed[A](a: => A): ToyZIO[Any, Nothing, A] =
    ToyZIO(_ => Right(a))

  def collectAll[R, E, A] (
    input: Iterable[ToyZIO[R, E, A]]
  ): ToyZIO[R, E, List[A]] =
    if (input.isEmpty) succeed(List.empty)
    else zipWith(input.head, collectAll(input.tail))(_ :: _)
}

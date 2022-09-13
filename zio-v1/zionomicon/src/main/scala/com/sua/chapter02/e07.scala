package com.sua.chapter02

import scala.annotation.tailrec

/** Implement the 'collectAll' function in terms of a ZIO effect toy's model.
  * The function should return an effect that sequentially collects the results
  * of the specified collection effects
  */

object e07CollectAll {
  final case class ToyZIO[-R, +E, +A](run: R => Either[E, A])

  def collectAll[R, E, A](
      input: Iterable[ToyZIO[R, E, A]],
  ): ToyZIO[R, E, List[A]] = {

    @tailrec
    def collect(
        input: Iterable[ToyZIO[R, E, A]],
        accumulator: Either[E, List[A]],
        environment: R,
    ): Either[E, List[A]] =
      if (input.isEmpty) accumulator
      else {
        input.head.run(environment) match {
          case Left(error) => Left(error)
          case Right(success) =>
            accumulator match {
              case left @ Left(_) => left
              case Right(list) =>
                collect(input.tail, Right(list :+ success), environment)
            }
        }

        ToyZIO { environment =>
          collect(input, Right(List.empty[A]), environment)
        }
      }
  }

  def collectAll[R, E, A](
      input: Iterable[ToyZIO[R, E, A]],
  ): ToyZIO[R, E, List[A]] =
    ???
}

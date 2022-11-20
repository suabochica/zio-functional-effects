package com.sua.chapter04

import zio.{Exit, ZIO}

/**
 * Using the `ZIO#run` method, which "runs" an effect to an `Exit`
 * value, implement the following function, which will execute the specified
 * effect on any failure at all:
 */

object e04OnAnyFailure {
  def onAnyFailure[R, E, A](
      zio: ZIO[R, E, A],
      handler: ZIO[R, E, Any]): ZIO[R, E, A] =
    zio.run.flatMap {
      case Exit.Failure(cause) => handler *> ZIO.halt(cause)
      case Exit.Success(a) => ZIO.succeed(a)
    }
}

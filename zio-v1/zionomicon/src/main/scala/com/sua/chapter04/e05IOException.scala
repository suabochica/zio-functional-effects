package com.sua.chapter04

import zio.ZIO

/**
 * Using the `ZIO#refineOrDie` method, implement the `ioException` function,
 * which refines the error channel to only include the `IOException` error.
 */

object e05IOException {
  def IOException[R, A](
    zio: ZIO[R, Throwable, A]
  ): ZIO[R, java.io.IOException, A] =
    zio.refineOrDie[java.io.IOException]
}

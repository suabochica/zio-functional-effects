package com.sua.chapter02

import zio.ZIO

/**
 * Using `ZIO.fail` and `ZIO.succeed`, implement the following function
 * which converts a `List` into a ZIO effect, by looking at the head element
 * in the lis and ignoring the rest of the elements
 */

object e12ListToZIO {
  def listToZIO[A](list: List[A]): ZIO[Any, None.type, A] =
    list match {
      case a :: _ => ZIO.succeed(a)
      case Nil => ZIO.fail(None)
    }
}

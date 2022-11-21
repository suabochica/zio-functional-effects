package com.sua.chapter04

import zio.ZIO

/**
 * Using the `ZIO#refineToOrDie` method, narrow the error type of the
 * following effect to just `NumberFormatException`.
 */

object e06NumberFormatException {
  val parseNumber: ZIO[Any, Throwable, Int] =
    ZIO.effect("foo".toInt).refineOrDie[NumberFormatException]
}
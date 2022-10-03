package com.sua.chapter02

import zio.ZIO

/** Using `ZIO.async`, convert the following asynchronous, callback-based
  * function into a ZIO function:
  */
object e14GetCacheValue {
  def getCacheValue(
      key: String,
      onSuccess: String => Unit,
      onFailure: Throwable => Unit,
  ): Unit =
    ???

  def getCacheValueZio(key: String): ZIO[Any, Throwable, String] =
    ZIO.effectAsync { callback =>
      getCacheValue(
        key,
        success => callback(ZIO.succeed(success)),
        failure => callback(ZIO.fail(failure)),
      )
    }
}

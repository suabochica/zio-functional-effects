package com.sua.chapter02

import zio.ZIO

object e14GetCacheValueZIO {
  def getCacheValue(
                   key: String,
                   onSuccess: String => Unit,
                   onFailure: Throwable => Unit
                   ) : Unit =
    ???

    def getCacheValueZIO(key:String): ZIO[Any,Throwable,String] =
      ZIO.async {callback =>
        getCacheValue(
          key, onSuccess => callback(ZIO.succeed(onSuccess)), onFailure => callback(ZIO.fail(onFailure))
        )
      }
}
package com.sua.chapter02

import zio.ZIO

/** Using `ZIO.effectTotal`, convert the following procedural function into a
  * ZIO function:
  */
object e13ProceduralToZIO {

  def currentTime(): Long = java.lang.System.currentTimeMillis()

  lazy val currentTimeZIO: ZIO[Any, Nothing, Long] =
    ZIO.effectTotal(currentTime())
}

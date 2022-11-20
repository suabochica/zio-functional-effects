package com.sua.chapter04

import zio.ZIO

object e03LogFailures {
  def logFailures[R, E, A](zio: ZIO[R, E, A]): ZIO[R, E, A] =
    zio.foldCauseM(
  cause => {
  cause.prettyPrint
  zio
},
  _ => zio

  )
}
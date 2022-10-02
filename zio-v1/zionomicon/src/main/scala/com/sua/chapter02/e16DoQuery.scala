package com.sua.chapter02

import zio.ZIO

/** Using `ZIO.fromFuture`, convert the following code to ZIO:
  */
object e16DoQuery {

  import scala.concurrent.{ExecutionContext, Future}
  trait Query
  trait Result

  def doQuery(query: Query)(implicit ec: ExecutionContext): Future[Result] =
    ???

  def doQueryZio(query: Query): ZIO[Any, Throwable, Result] =
    ZIO.fromFuture(ec => doQuery(query)(ec))
}

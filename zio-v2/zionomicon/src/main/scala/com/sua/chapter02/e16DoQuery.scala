package com.sua.chapter02

import zio.ZIO
import scala.concurrent.{ExecutionContext, Future}

/**
 * Using `ZIO.fromFuture`, convert the following code to ZIO
 */

object e16DoQuery {
  trait Query
  trait Result
  def doQuery(query: Query)(implicit executionContext: ExecutionContext): Future[Result] =
    ???

  def doQueryZIO(query: Query): ZIO[Any, Throwable, Result] =
    ZIO.fromFuture(executionContext => doQuery(query)(executionContext))
}
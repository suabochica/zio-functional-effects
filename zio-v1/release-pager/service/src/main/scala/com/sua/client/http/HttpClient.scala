package com.sua.client.http

import io.circe.Decoder
import zio.{ Has, IO, Task, URLayer, ZLayer }

object HttpClient {
  type HttpClient = Has[Service]

  trait Service {
    def get[T](uri: String)(implicit d: Decoder[T]) IO[PageError, T]
  }

  def http4s: URLayer[Has[Client[Task]], Has[Service]] =
    ???
}
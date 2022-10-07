package com.sua.client.http

// imports from domain
import com.sua.PagerError

// imports from external libraries
import io.circe.Decoder
import org.http4s.client.Client
import zio.{ Has, IO, Task, URLayer, ZLayer }

object HttpClient {
  type HttpClient = Has[Service]

  trait Service {
    def get[T](uri: String)(implicit d: Decoder[T]): IO[PagerError, T]
  }

  def http4s: URLayer[Has[Client[Task]], Has[Service]] =
    ZLayer.fromService[Client[Task], Service] { http4sClient: Client[Task] =>
      Http4s(http4sClient)
    }
}

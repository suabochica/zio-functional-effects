package com.sua.client.http

// imports from domain
import com.sua.PagerError
import com.sua.PagerError.{ MalformedUrl, NotFound }

// imports from external libraries
import io.circe.{ Decoder, Encoder }
import org.http4s.circe.{ jsonEncoderOf, jsonOf }
import org.http4s.client.Client
import org.http4s.{ EntityDecoder, EntityEncoder, Uri }
import zio.{ IO, Task, ZIO }

final private[http] case class Http4s(client: Client[Task])
    extends HttpClient.Service {
  implicit def entityDecoder[A](implicit
    decoder: Decoder[A]
  ): EntityDecoder[Task, A] = jsonOf[Task, A]
  implicit def entityEncoder[A](implicit
    encoder: Encoder[A]
  ): EntityEncoder[Task, A] = jsonEncoderOf[Task, A]

  def get[T](uri: String)(implicit decoder: Decoder[T]): IO[PagerError, T] = {
    def call(uri: Uri): IO[PagerError, T] =
      client
        .expect[T](uri)
        .foldM(
          _ => IO.fail(NotFound(uri.renderString)),
          result => ZIO.succeed(result)
        )

    Uri.fromString(uri).fold(_ => IO.fail(MalformedUrl(uri)), call)
  }
}

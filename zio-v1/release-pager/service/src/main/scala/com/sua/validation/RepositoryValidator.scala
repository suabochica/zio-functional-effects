package com.sua.validation

import com.sua.PagerError
import com.sua.client.github.GitHubClient
import com.sua.log.Logger

import zio.{ Has, IO, ZIO, ZLayer }

object RepositoryValidator {
  type RepositoryValidator = Has[Service]

  trait Service {
    def validate(text: String): IO[PagerError, Name]
  }

  type LiveDependencies = Logger with GitHubClient

  def live: ZLayer[LiveDependencies, Nothing, Has[Service]] =
    ???
}
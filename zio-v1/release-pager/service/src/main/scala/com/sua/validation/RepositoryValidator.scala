package com.sua.validation

// imports from domain
import com.sua.PagerError
import com.sua.subscription.Repository.Name

// imports from service
import com.sua.client.github.GitHubClient
import com.sua.client.github.GitHubClient.GitHubClient
import com.sua.log.Logger
import com.sua.log.Logger.Logger

import zio.{ Has, IO, ZLayer }

object RepositoryValidator {
  type RepositoryValidator = Has[Service]

  trait Service {
    def validate(text: String): IO[PagerError, Name]
  }

  type LiveDependencies = Logger with GitHubClient

  def live: ZLayer[LiveDependencies, Nothing, Has[Service]] =
    ZLayer.fromServices[Logger.Service, GitHubClient.Service, Service] {
      (logger, gitHubClient) =>
        RepositoryValidatorLive(logger, gitHubClient)
    }
}

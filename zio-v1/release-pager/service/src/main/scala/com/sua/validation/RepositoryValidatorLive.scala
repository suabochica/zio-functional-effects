package com.sua.validation
// imports from domain
import com.sua.PagerError
import com.sua.subscription.Repository.Name

// imports from service
import com.sua.client.github.GitHubClient
import com.sua.log.Logger

// imports from external libraries
import zio.IO

final private[validation] case class RepositoryValidatorLive(
  logger: Logger.Service,
  gitHubClient: GitHubClient.Service
) extends RepositoryValidator.Service {

  def validate(name: String): IO[PagerError, Name] =
    gitHubClient
      .repositoryExists(Name(name))
      .foldM(
        error =>
          logger.info(s"Failed to find repository $name") *> IO.fail(error),
        context =>
          logger.info(s"validated repository $name") *> IO.succeed(context)
      )

}

package com.sua.validation

private[validation] final case class RepositoryValidatorLive(
  logger: Logger.Service,
  gitHubClient: GitHubClient.Service
) extends RepositoryValidator.Service {

  def validate(name: String): IO[PagerError, Name] =
    ???

}
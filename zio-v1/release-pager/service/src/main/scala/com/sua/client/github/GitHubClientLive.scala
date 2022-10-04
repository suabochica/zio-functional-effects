package com.sua.client.github

private[github] final case class GitHubClientLive(logger: Logger.Service, httpClient: HttpClient.Service) extends GitHubClient.Service {
  override def repositoryExist(name: Name): IO[PageError, Name] =
    ???

  override def releases(name: Name): IO[PageError, List[GitHubRelease]] =
    ???
}

private[github] object GitHubClientLive {
  // Decoder
  ???
}
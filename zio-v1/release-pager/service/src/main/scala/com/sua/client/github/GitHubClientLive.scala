package com.sua.client.github

// import from domain
import com.sua.client.github.GitHubRelease
import com.sua.subscription.Repository.{ Name, Version }
import com.sua.PagerError
import com.sua.PagerError.NotFound

// import from storage
// import from service
import com.sua.client.github.GitHubClientLive.{
  githubReleaseDecoder,
  versionDecoder
}
import com.sua.client.http.HttpClient
import com.sua.log.Logger

// import from external libraries
import io.circe.Decoder
import io.circe.generic.extras.semiauto.deriveUnwrappedDecoder
import io.circe.generic.semiauto.deriveDecoder
import zio.{ IO, ZIO }

final private[github] case class GitHubClientLive(
  logger: Logger.Service,
  httpClient: HttpClient.Service
) extends GitHubClient.Service {
  override def repositoryExists(name: Name): IO[PagerError, Name] =
    releases(name).as(name)

  override def releases(name: Name): IO[PagerError, List[GitHubRelease]] = {
    val url = s"https://api.github.com/repos/${name.value}/releases"

    logger.info(s"Checking releases of ${name.value}: $url") *>
      httpClient
        .get[List[GitHubRelease]](url)
        .foldM(
          error =>
            logger.warn(
              s"Could not find repository ${name.value}: $error"
            ) *> ZIO.fail(NotFound(name.value)),
          releases => ZIO.succeed(releases)
        )
  }
}

private[github] object GitHubClientLive {
  // Decoder
  implicit val versionDecoder: Decoder[Version]             = deriveUnwrappedDecoder
  implicit val githubReleaseDecoder: Decoder[GitHubRelease] = deriveDecoder
}

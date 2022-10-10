package com.sua.checker

// imports from domain
import com.sua.PagerError
import com.sua.subscription.Repository.{ Name, Version }

// imports from service
import com.sua.client.github.GitHubClient
import com.sua.client.telegram.TelegramClient
import com.sua.log.Logger
import com.sua.subscription.{ Repository, SubscriptionLogic }

// imports from external libraries
import scala.util.Try
import zio.{ IO, Task, ZIO }

final private[checker] case class ReleaseCheckerLive(
  logger: Logger.Service,
  gitHubClient: GitHubClient.Service,
  telegramClient: TelegramClient.Service,
  subscriptionLogic: SubscriptionLogic.Service
) extends ReleaseChecker.Service {

  override def scheduleRefresh: Task[Unit] =
    for {
      _              <- logger.info("Getting latest repository versions")
      repositories   <- subscriptionLogic.listRepositories
      latestVersions <- getLatestRepositoryVersions(repositories.keySet)
      updatedVersions = compareVersions(repositories, latestVersions)
      _              <- subscriptionLogic.updateVersions(updatedVersions)
      statuses       <- getRepositoryStates(updatedVersions)
      _              <- broadcastUpdates(statuses)
      _              <- logger.info("Finished repository refresh")
    } yield ()

  private def getRepositoryStates(
    updatedVersions: Map[Name, Version]
  ): Task[List[Repository]] =
    ZIO.foreach(updatedVersions.toList) { case (name, version) =>
      subscriptionLogic
        .listSubscribers(name)
        .map(subscribers => Repository(name, version, subscribers))
    }

  private def getLatestRepositoryVersions(
    repositories: Set[Name]
  ): IO[PagerError, Map[Name, Option[Version]]] =
    ZIO
      .foreach(repositories) { name =>
        gitHubClient
          .releases(name)
          .map(releases =>
            name -> Try(releases.maxBy(_.published_at).name).toOption
          )
      }
      .map(_.toMap)

  private def compareVersions(
    latestKnownReleases: Map[Name, Option[Version]],
    latestReleases: Map[Name, Option[Version]]
  ): Map[Name, Version] =
    latestKnownReleases.flatMap { case (name, latestKnownVersion) =>
      latestReleases
        .get(name)
        .collect {
          case Some(latestVersion)
              if latestKnownVersion.contains(latestVersion) =>
            name -> latestVersion
        }
    }

  private def broadcastUpdates(repositories: List[Repository]): Task[Unit] =
    ZIO
      .foreach(repositories) { repository =>
        val message =
          s"There is a new version of ${repository.name.value} available: ${repository.version.value}"
        telegramClient.broadcastMessage(repository.subscribers, message)
      }
      .unit
}

package com.sua.checker

import zio.{ IO, Task, ZIO }

private[checker] final case class ReleaseCheckerLive(
  logger: Logger.Service,
  gitHubClient: GitHubClient.Service,
  telegramCliente: TelegramClient.Service,
  subscription: Subscription.Service
) extends ReleaseChecker.Service {

  override def schedulerefresh: Task[Unit] =
    ???

  private def getRepositoryStates(updatedVersions: Map[Name, Version]): Task[List[Repository]] =
    ???

  private def getLatestRepositoryVersions(repositories: Set[Name]): IO[PageError, Map[Name, Option[Version]]] =
    ???

  private def compareVersions(
    latestKnownReleases: Map[Name, Option[Versions]],
    latestReleases: Map[Name, Option[Versions]],
  ): Map[Name, Version] =
    ???

  private def broadcastUpdates(repositories: List[Repository]): Task[Unit] =
    ???
}
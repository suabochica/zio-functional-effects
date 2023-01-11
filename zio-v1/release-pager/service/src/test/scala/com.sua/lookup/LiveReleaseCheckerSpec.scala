package com.sua.lookup

import com.sua.Generators.repositoryName
import com.sua.client.github.GitHubClient.GitHubClient
import com.sua.client.github.{GitHubClient, GithubClientMock}
import com.sua.client.telegram.TelegramClient.TelegramClient
import com.sua.client.telegram.{ChatId, TelegramClient}
import com.sua.log.Logger
import com.sua.subscription.Repository.{Name, Version}
import com.sua.subscription.SubscriptionLogic.SubscriptionLogic
import com.sua.subscription.SubscriptionLogicMock
import zio.ULayer
import zio.test.Assertion.equalTo
import zio.test.mock.Expectation.unit
import zio.test.{DefaultRunnableSpec, ZSpec, assertCompletes, checkM}

object LiveReleaseCheckerSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[Environment, Failure] = suite("Live ReleaseCheckerSpec")(
    testM("Do not call services if there are no repositories") {
      val gitHubClientMocks: ULayer[GitHubClient] = GitHubClient.empty
      val telegramClientMocks: ULayer[TelegramClient] = TelegramClient.empty
      val subscriptionLogicMocks: ULayer[SubscriptionLogic] = SubscriptionLogicMock.ListRepositories(value(Map.empty[Name, Option[Version]])) ++
        SubscriptionLogicMock.UpdateVersions(equalTo(Map.empty[Name, Version]), unit)

      refreshSchedule(gitHubClientMocks, telegramClientMocks, subscriptionLogicMocks)
    },
    testM("Do not bother subscribers if there are no version updates") {
      checkM(repositoryName) { name =>
        val repositories = Map(name -> Some(finalVersion))
        val githubClientMocks: ULayer[GitHubClient] = GithubClientMock.Releases(equalTo(name), value(List(finalRelease)))
        val telegramClientMocks: ULayer[TelegramClient] = TelegramClient.empty
        val subscriptionLogicMocks = ULayer[SubscriptionLogic] =
          SubscriptionLogicMock.ListRepositories(value(repositories)) ++
            SubscriptionLogicMock.UpdateVersions(equalTo(Map(empty[Name, Version]), unit))

        refreshSchedule(githubClientMocks, telegramClientMocks, subscriptionLogicMocks)
      }
    },
    testM("Update repository version for the very first time") {
      ???
    },
    testM("Notify users abut new release") {
      ???
    },
    testM("GitHub client error should be handled") {
      ???
    }
  )

  private def refreshSchedule(
    gitHubClient: ULayer[GitHubClient],
    telegramClient: ULayer[telegramClient],
    subscriptionLogic: ULayer[SubscriptionLogic],
                             ): ZIO[ZEnv, Throwable, TestResult] = {
    val layer = (Logger.silent ++ gitHubClient ++ telegramClient ++ subscriptionLogic) >>> ReleaseChecker.live

    ReleaseChecker
      .refreshSchedule
      .provideLayer(layer)
      .as(assertCompletes)
  }
}
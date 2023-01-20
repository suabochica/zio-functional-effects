package com.sua.lookup

import com.sua.Generators.repositoryName
import com.sua.client.github.GitHubClient.GitHubClient
import com.sua.client.github.{GitHubClient, GitHubClientMock}
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
        val githubClientMocks: ULayer[GitHubClient] = GitHubClientMock.Releases(equalTo(name), value(List(finalRelease)))
        val telegramClientMocks: ULayer[TelegramClient] = TelegramClient.empty
        val subscriptionLogicMocks = ULayer[SubscriptionLogic] =
          SubscriptionLogicMock.ListRepositories(value(repositories)) ++
            SubscriptionLogicMock.UpdateVersions(equalTo(Map(empty[Name, Version]), unit))

        refreshSchedule(githubClientMocks, telegramClientMocks, subscriptionLogicMocks)
      }
    },
    testM("Update repository version for the very first time") {
      checkM(repositoryName) { name =>
        val repositories = Map(name -> None)
        val githubClientMocks: ULayer[GitHubClient] = GitHubClientMock.Releases(equalTo(name), value(List(finalRelease)))
        val telegramClientMocks: ULayer[TelegramClient] = TelegramClient.BroadcastMessage(equalTo(Set.empty[ChatId], message(name)), unit)
        val subscriptionLogicMocks = ULayer[SubscriptionLogic] =
          SubscriptionLogicMock.ListRepositories(value(repositories)) ++
          SubscriptionLogicMock.UpdateVersions(equalTo(Map(name -> finalVersion)), unit))
          SubscriptionLogicMock.ListSubscribers(equalTo(name), value(Set.empty))

        refreshSchedule(githubClientMocks, telegramClientMocks, subscriptionLogicMocks)
      }
    },
    testM("Notify users abut new release") {
      checkM(repositoryName, chatIds) { case (name, (chatId1, chatId2)) =>
        val repositories = Map(name -> Some(rcVersion))
        val subscribers = Set(chatId1, chatId2)

        val gitHubClientMocks = GitHubClientMock.Releases(equalTo(name), value(releases))
        val telegramClientMocks = TelegramClientMock.BroadcastMessage(equalTo((subscribers, message(name))), unit)
        val subscriptionLogicMocks =
          SubscriptionLogicMock.ListRepositories(value(repositories)) ++
            SubscriptionLogicMock.UpdateVersions(equalTo(Map(name -> finalVersion)), unit) ++
            SubscriptionLogicMock.ListSubscribers(equalTo(name), value(subscribers))

        scheduleRefresh(gitHubClientMocks, telegramClientMocks, subscriptionLogicMocks)
      }
    },
    testM("GitHub client error should be handled") {
      checkM(repositoryName) { name =>
        val repositories = Map(name -> Some(rcVersion))
        val error = NotFound(name.value)
        val gitHubClientMocks = GitHubClientMock.Releases(equalTo(name), failure(error))
        val telegramClientMocks = TelegramClient.empty
        val subscriptionLogicMocks = SubscriptionLogicMock.ListRepositories(value(repositories))

        val result = scheduleRefresh(gitHubClientMocks, telegramClientMocks, subscriptionLogicMocks)

        assertM(result.run)(fails(equalTo(error)))
      }
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
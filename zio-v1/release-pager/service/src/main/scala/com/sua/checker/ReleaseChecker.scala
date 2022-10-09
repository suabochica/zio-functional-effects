package com.sua.checker

// imports from service
import com.sua.client.github.GitHubClient
import com.sua.client.github.GitHubClient.GitHubClient
import com.sua.client.telegram.TelegramClient
import com.sua.client.telegram.TelegramClient.TelegramClient
import com.sua.log.Logger
import com.sua.log.Logger.Logger
import com.sua.subscription.SubscriptionLogic
import com.sua.subscription.SubscriptionLogic.SubscriptionLogic

// import from externals libraries
import zio.{ Has, Task, URLayer, ZLayer }

object ReleaseChecker {
  type ReleaseChecker = Has[Service]

  trait Service {
    def scheduleRefresh: Task[Unit]
  }

  type LiveDependencies = Logger
    with GitHubClient
    with TelegramClient
    with SubscriptionLogic

  def live: URLayer[LiveDependencies, Has[Service]] =
    ZLayer.fromServices[
      Logger.Service,
      GitHubClient.Service,
      TelegramClient.Service,
      SubscriptionLogic.Service
    ] { (logger, githubClient, telegramClient, subscriptionLogic) =>
      ReleaseCheckerLive(logger, gitHubClient, telegramClient, subscription)
    }
}

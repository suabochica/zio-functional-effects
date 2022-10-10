package com.sua.subscription

// Imports from domain
import com.sua.client.telegram.ChatId
import com.sua.subscription.Repository.{ Name, Version }

// Imports from storage
import com.sua.subscription.chat.ChatStorage
import com.sua.subscription.chat.ChatStorage.ChatStorage
import com.sua.subscription.repository.RepositoryVersionStorage
import com.sua.subscription.repository.RepositoryVersionStorage.RepositoryVersionStorage

// Imports from services
import com.sua.log.Logger
import com.sua.log.Logger.Logger

import zio.{ Has, Task, URLayer, ZLayer }

object SubscriptionLogic {
  type SubscriptionLogic = Has[Service]

  trait Service {
    def subscribe(chatId: ChatId, name: Name): Task[Unit]
    def unsubscribe(chatId: ChatId, name: Name): Task[Unit]
    def listRepositories: Task[Map[Name, Option[Version]]]
    def listSubscriptions(chatId: ChatId): Task[Set[Name]]
    def listSubscribers(name: Name): Task[Set[ChatId]]
    def updateVersions(updateVersions: Map[Name, Version]): Task[Unit]
  }

  type LiveDependencies = Logger with ChatStorage with RepositoryVersionStorage

  def live: URLayer[LiveDependencies, Has[Service]] =
    ZLayer.fromServices[
      Logger.Service,
      ChatStorage.Service,
      RepositoryVersionStorage.Service,
      Service
    ] { (logger, chatStorage, repositoryStorage) =>
      SubscriptionLogicLive(logger, chatStorage, repositoryStorage)
    }
}

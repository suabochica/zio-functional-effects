package com.sua.subscription

// Imports from domain
import com.sua.client.telegram.ChatId
import com.sua.subscription.Repository.{ Name, Version }

// Imports from storage
import com.sua.subscription.chat.ChatStorage
import com.sua.subscription.chat.RepositoryVersionStorage

// Imports from services
import com.sua.log.Logger

import zio.{ Has, URLayer, ZIO, ZLayer }

object Subscription {
  type Subscription = Has[Service]

  trait Service {
    def subscribe(chatId: ChatId, name: Name): Task[Unit]
    def unsubscribe(chatId: ChatId, name: Name): Task[Unit]
    def listSubscriptions(chatId: ChatId): Task[Set[Name]]
    def listSubscribers(name: Name): Task[Set[ChatId]]
    def updateVersions(updateVersions: Map[Name, Version]): Task[Unit]
  }

  type LiveDependencies = Logger with ChatStorage with RepositoryVersionStorage

  def live: URLayer[LiveDependencies, Has[Service]] =
    Zlayer.fromServices[
      Logger.Service,
      ChatStorage.Service,
      RepositoryVersionStorage.Service,
      Service
    ] {
      (logger, chatStorage, repositoryStorage) =>
        SubscriptionLive(logger, chatStorage, repositoryStorage)
    }
}
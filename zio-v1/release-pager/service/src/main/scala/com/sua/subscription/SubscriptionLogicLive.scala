package com.sua.subscription

// imports from domain
import com.sua.client.telegram.ChatId
import com.sua.subscription.Repository.{ Name, Version }

// imports from storage
import com.sua.subscription.chat.ChatStorage
import com.sua.subscription.repository.RepositoryVersionStorage

// imports from service
import com.sua.log.Logger

// imports from external libraries
import zio.{ Task, ZIO }

final private[subscription] case class SubscriptionLogicLive(
  logger: Logger.Service,
  chatStorage: ChatStorage.Service,
  repositoryVersionStorage: RepositoryVersionStorage.Service
) extends SubscriptionLogic.Service {

  override def subscribe(chatId: ChatId, name: Name): Task[Unit] =
    logger.info(s"$chatId subscribed to $name") *>
      chatStorage.subscribe(chatId, name) *>
      repositoryVersionStorage.addRepository(name)

  override def unsubscribe(chatId: ChatId, name: Name): Task[Unit] =
    logger.info(s"Chat $chatId unsubscribed from $name") *>
      chatStorage.unsubscribe(chatId, name)

  override def listRepositories: Task[Set[ChatId]] =
    logger.info(s"Listing repositories") *>
      repositoryVersionStorage.listRepositories

  override def listSubscriptions(chatId: ChatId): Task[Set[Name]] =
    logger.info(s"Chat $chatId requested subscriptions") *>
      chatStorage.listSubscriptions(chatId)

  override def listSubscribers(name: Name): Task[Set[ChatId]] =
    logger.info(s"Listing repositories ${name.value} subscribers") *>
      chatStorage.listSubscribers(name)

  override def updateVersions(updatedVersions: Map[Name, Version]): Task[Unit] =
    ZIO
      .foreach(updatedVersions.toList) { case (name, version) =>
        logger.info(
          s"Updating repository ${name.value} version to $version"
        ) *>
          repositoryVersionStorage.updateRepositoryVersion(name, version)
      }
      .unit

}

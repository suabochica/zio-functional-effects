package com.sua.subscription

private[subscription] final case class SubscriptionLive(
  logger: Logger.Service,
  chatStorage: ChatStorage.Service,
  repositoryVersionStorage: RepositoryVersionStorage.Service
) extends Subscription.Service {

  override def subscribe(chatId: ChatId, name: Name): Task[Unit] =
    ???

  override def unsubscribe(chatId: ChatId, name: Name): Task[Unit] =
    ???

  override def listSubscriptions(chatId: ChatId): Task[Set[Name]] =
    ???

  override def listSubscribers(name: Name): Task[Set[ChatId]] =
    ???

  override def updateVersions(updateVersions: Map[Name, Version]): Task[Unit] =
    ???

}

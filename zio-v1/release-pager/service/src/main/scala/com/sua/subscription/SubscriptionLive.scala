package com.sua.subscription

private[subscription] final case class SubscriptionLive(
  logger: Logger.Service,
  chatStorage: ChatStorage.Service,
  repositoryVersionStorage: RepositoryVersionStorage.Service
) extends Subscription.Service {

  override def subscribe(chatId: ChatId, name: Name): Task[Unit] =
    logger.info(s"$chatId subscribed to $name") *>
      chatStorage.subscribe(chatId, name) *>
      repositoryVersionStorage.addRepository(name)

  override def unsubscribe(chatId: ChatId, name: Name): Task[Unit] =
    logger.info(s"Chat $chatId unsubscribed from $name") *>
      chatStorage.unsubscribe(chatId, name)

  override def listSubscriptions(chatId: ChatId): Task[Set[Name]] =
    logger.info(s"Chat $chatId requested subscriptions") *>
      chatStorage.listSubscriptions(chatId, name)

  override def listRepositories: Task[Set[ChatId]] =
    logger.info(s"Listing repositories") *>
      repositoryVersionStorage.listRepositories

  override def listSubscribers(name: Name): Task[Set[ChatId]] =
  logger.info(s"Listing repositories ${name.value} subscribers") *>
    chatStorage.listSubscribers(name)

  override def updateVersions(updatedVersions: Map[Name, Version]): Task[Unit] =
    ZIO.foreach(updatedVersions.toList) {
      case (name, version) =>
        logger.info(s"Updating repository ${name.value} version to ${version}") *>
        repositoryVersionStorage.updateVerison(name, version)
    }
      .unit

}

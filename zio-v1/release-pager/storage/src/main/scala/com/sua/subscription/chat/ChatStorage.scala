package com.sua.subscription.chat

object ChatStorage {
  type ChatStorage = Has[Service]
  type SubscriptionMap = Map[ChatId, Set[Name]]

  trait Service {
    def subscribe(chatId: ChatId, name: Name): Task[Unit]
    def unsubscribe(chatId: ChatId, name: Name): Task[Unit]
    def listSubscriptions(chatId: ChatId): Task[Set[Name]]
    def listSubscribers(name: Name): Task[Set[ChatId]]
  }

  val inMemory: ZLayer[Has[Ref[SubscriptionMap]], Nothing, Has[Service]] = ???

  val doobie: ZLayer[Has[Transactor[Task]], Nothing, Has[Service]] = ???
}
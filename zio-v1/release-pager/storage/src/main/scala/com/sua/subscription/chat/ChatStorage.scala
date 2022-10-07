package com.sua.subscription.chat

// imports from domain
import com.sua.subscription.Repository.Name

// imports from service
import com.sua.client.telegram.ChatId

// imports from external libraries
import doobie.util.transactor.Transactor
import zio.{ Has, Ref, Task, ZLayer }

object ChatStorage {
  type ChatStorage     = Has[Service]
  type SubscriptionMap = Map[ChatId, Set[Name]]

  trait Service {
    def subscribe(chatId: ChatId, name: Name): Task[Unit]
    def unsubscribe(chatId: ChatId, name: Name): Task[Unit]
    def listSubscriptions(chatId: ChatId): Task[Set[Name]]
    def listSubscribers(name: Name): Task[Set[ChatId]]
  }

  val inMemory: ZLayer[Has[Ref[SubscriptionMap]], Nothing, Has[Service]] =
    ZLayer.fromService[Ref[SubscriptionMap], Service] { subscriptions =>
      InMemory(subscriptions)
    }

  val doobie: ZLayer[Has[Transactor[Task]], Nothing, Has[Service]] = ???
}

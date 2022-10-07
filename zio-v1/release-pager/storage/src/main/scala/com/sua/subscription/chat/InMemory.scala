package com.sua.subscription.chat

// imports from domain
import com.sua.client.telegram.ChatId
import com.sua.subscription.Repository.Name

// imports from storage
import com.sua.subscription.chat.ChatStorage.{ Service, SubscriptionMap }

import zio.{ Ref, UIO }

final private[chat] case class InMemory(subscriptions: Ref[SubscriptionMap])
    extends Service {
  type RepositoryUpdate = Set[Name] => Set[Name]

  private def updateSubscriptions(
    chatId: ChatId
  )(f: RepositoryUpdate): UIO[Unit] =
    subscriptions.update { current =>
      val subscriptions = current.getOrElse(chatId, Set.empty)

      current + (chatId -> f(subscriptions))
    }.unit

  override def subscribe(chatId: ChatId, name: Name): UIO[Unit] =
    updateSubscriptions(chatId)(_ + name).unit

  override def unsubscribe(chatId: ChatId, name: Name): UIO[Unit] =
    updateSubscriptions(chatId)(_ - name).unit

  override def listSubscriptions(chatId: ChatId): UIO[Set[Name]] =
    subscriptions
      .get
      .map(_.getOrElse(chatId, Set.empty))

  override def listSubscribers(name: Name): UIO[Set[ChatId]] =
    subscriptions
      .get
      .map(_.collect {
        case (chatId, repositories) if repositories.contains(name) => chatId
      }.toSet)
}

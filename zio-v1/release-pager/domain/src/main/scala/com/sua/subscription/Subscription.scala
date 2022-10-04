package com.sua.subscription

final case class Subscription(id: SubscriptionId, chatId: ChatId, name: Name)

object Subscription {
  final case class SubscriptionId(value: String)

  def make(chatId: ChatId, name: Name): Subscription =
    Subscription(
      SubscriptionId(UUID.randomUUID().toString),
      chatId,
      name
    )
}
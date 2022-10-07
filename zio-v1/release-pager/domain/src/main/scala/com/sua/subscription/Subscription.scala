package com.sua.subscription

// imports from domain
import com.sua.subscription.Repository.Name
import com.sua.subscription.Subscription.SubscriptionId

// imports from service
import com.sua.client.telegram.ChatId

// imports from external libraries
import java.util.UUID

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

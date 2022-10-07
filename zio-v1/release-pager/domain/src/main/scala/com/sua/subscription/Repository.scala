package com.sua.subscription

// imports from domain
import com.sua.subscription.Repository.{ Name, Version }

// imports from service
import com.sua.client.telegram.ChatId

final case class Repository(
  name: Name,
  version: Version,
  subscribers: Set[ChatId]
)

object Repository {
  final case class Name(value: String)
  final case class Version(value: String)
}

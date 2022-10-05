package com.sua.subscription

import com.sua.client.telegram.ChatId
import com.sua.subscription.Repository{ Name, Version }

final case class Repository(
  name: Name,
  version: Version,
  subscribers: Set[ChatId]
)

object Repository {
  final case class Name(value: String)
  final case class Version(value: String)
}
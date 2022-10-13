package com.sua.subscription

// imports from domain
import com.sua.subscription.Repository.{ Name, Version }

final case class RepositoryVersion(
  name: Name,
  version: Option[Version]
)

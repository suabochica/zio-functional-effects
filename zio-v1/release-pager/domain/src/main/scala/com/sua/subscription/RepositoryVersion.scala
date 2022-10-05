package com.sua.subscription

import com.sua.Repository{ Name, Version }

final case class RepositoryVersion(
  name: Name,
  version: Option[Version],
)

package com.sua

import com.sua.client.github.GitHubRelease
import com.sua.subscription.Repository.{Name, Version}

import java.time.Instant

object TestData {
  val finalVersion: Version = Version("1.0.0")
  val rcVersion: Version = Version("1.0.0-RC17")

  val timestamp1: Instant = Instant.parse("2019-11-29T17:37:23.00Z")
  val timestamp2: Instant = Instant.parse("2019-12-29T18:37:25.00Z")

  val rcRelease: GitHubRelease = GitHubRelease(rcVersion, timestamp1)
  val finalRelease: GitHubRelease = GitHubRelease(finalVersion, timestamp2)
  val releases: List[GitHubRelease] = List(rcRelease, finalRelease)

  def message(name: Name) = s"There is a new version of ${name.value} available: ${finalVersion.value}"
}
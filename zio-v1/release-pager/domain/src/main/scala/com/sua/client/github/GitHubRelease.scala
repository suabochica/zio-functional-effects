package com.sua.client.github

// import from domain
import com.sua.subscription.Repository.Version

// import from external libraries
import java.time.Instant

final case class GitHubRelease(name: Version, published_at: Instant)

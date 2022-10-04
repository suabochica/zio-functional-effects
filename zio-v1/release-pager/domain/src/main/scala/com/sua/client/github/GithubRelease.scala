package com.sua.client.github

import java.time.Instant

final case class GithubRelease(name: Version, published_at: Instant)
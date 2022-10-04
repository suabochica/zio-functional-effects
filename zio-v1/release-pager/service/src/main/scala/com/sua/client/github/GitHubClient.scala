package com.sua.client.github

import zio.{ Has, IO, Task, URLayer, ZLayer }

object GitHubClient {
  type GitHubClient = Has[Service]

  trait Service {
    def repositoryExist(name: Name): IO[PageError, Name]
    def releases(name: Name): IO[PageError, List[GitHubRelease]]
  }

  def live: URLayer[Logger with HttpClient, Has[Service]] = ???

  def empty: ULayer[Has[Service]] = ???
}
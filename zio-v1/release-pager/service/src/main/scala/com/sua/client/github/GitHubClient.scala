package com.sua.client.github

// import from domain
import com.sua.PagerError
import com.sua.client.github.GitHubRelease
import com.sua.subscription.Repository.Name

// import from service
import com.sua.client.http.HttpClient
import com.sua.client.http.HttpClient.HttpClient
import com.sua.log.Logger
import com.sua.log.Logger.Logger

// import from external libraries
import zio.{ Has, IO, Task, ULayer, URLayer, ZLayer }

object GitHubClient {
  type GitHubClient = Has[Service]

  trait Service {
    def repositoryExist(name: Name): IO[PagerError, Name]
    def releases(name: Name): IO[PagerError, List[GitHubRelease]]
  }

  def live: URLayer[Logger with HttpClient, Has[Service]] =
    ZLayer.fromServices[Logger.Service, HttpClient.Service, Service] {
      (logger, httpClient) => GitHubClientLive(logger, httpClient)
    }

  def empty: ULayer[Has[Service]] =
    ZLayer.succeed(new Service {
      override def repositoryExist(name: Name): IO[PagerError, Name]         = ???
      override def releases(name: Name): IO[PagerError, List[GitHubRelease]] =
        ???
    })
}

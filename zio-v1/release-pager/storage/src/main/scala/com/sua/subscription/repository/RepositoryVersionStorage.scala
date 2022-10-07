package com.sua.subscription.repository

// imports from domain
import com.sua.subscription.Repository.{ Name, Version }

// imports from external libraries
import doobie.util.transactor.Transactor
import zio.{ Has, Ref, Task, UIO, ZLayer }

object RepositoryVersionStorage {
  type RepositoryVersionStorage = Has[Service]
  type RepositoryVersionMap     = Map[Name, Option[Version]]

  trait Service {
    def addRepository(name: Name): UIO[Unit]
    def updateRepositoryVersion(name: Name, version: Version): UIO[Unit]
    def deleteRepository(name: Name): UIO[Unit]
    def listRepositories: UIO[RepositoryVersionMap]
  }

  val inMemory: ZLayer[Has[Ref[RepositoryVersionMap]], Nothing, Has[Service]] =
    ZLayer.fromService[Ref[RepositoryVersionMap], Service] { subscriptions =>
      InMemory(subscriptions)
    }

  val doobie: ZLayer[Has[Transactor[Task]], Nothing, Has[Service]] = ???
}

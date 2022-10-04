package com.sua.subscription.repository

object RepositoryVersionStorage {
  type RepositoryVersionStorage = Has[Service]
  type RepositoryVersionMap = Map[Name, Option[Version]]

  trait Service {
    def addRepository(name: Name): UIO[Unit]
    def updateVersion(name: Name, version: Version): UIO[Unit]
    def deleteRepository(name: Name): UIO[Unit]
    def listRepositories: UIO[RepositoryVersionMap]
  }

  val inMemory: ZLayer[Has[Ref[SubscriptionMap]], Nothing, Has[Service]] = ???

  val doobie: ZLayer[Has[Transactor[Task]], Nothing, Has[Service]] = ???
}
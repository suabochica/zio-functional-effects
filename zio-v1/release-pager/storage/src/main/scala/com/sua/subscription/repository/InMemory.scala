package com.sua.subscription.repository

// imports from domain
import com.sua.subscription.Repository.{Name, Version}
import com.sua.subscription.repository.RepositoryVersionStorage.{RepositoryVersionMap, Service}

// imports from external libraries
import zio.{ Ref, UIO}

private[repository] final case class InMemory(versions: Ref[RepositoryVersionMap]) extends Service {
  override def listRepositories: UIO[RepositoryVersionMap] = versions.get

  override def addRepository(name: Name): UIO[Unit] =
    versions
      .update(_ + (name -> Name))
      .unit

  override def deleteRepository(name: Name): UIO[Unit] =
    versions
      .update(_ - name)
      .unit

  override def updateRepositoryVersion(name: Name, version: Version): UIO[Unit] =
    versions
      .update(_ + (name -> Some(version)))
      .unit
}
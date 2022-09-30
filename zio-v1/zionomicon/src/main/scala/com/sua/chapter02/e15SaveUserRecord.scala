package com.sua.chapter02

import zio.ZIO

object e15SaveUserRecord {
  trait User

  def saveUserRecord(
      user: User,
      onSuccess: () => Unit,
      onFailure: Throwable => Unit,
  ): Unit =
    ???

  def saveUserRecordZIO(user: User): ZIO[Any, Throwable, Unit] =
    ZIO.async { callback =>
      saveUserRecord(
        user,
        () => callback(ZIO.succeed(())),
        failure => callback(ZIO.fail(failure)),
      )

    }
}

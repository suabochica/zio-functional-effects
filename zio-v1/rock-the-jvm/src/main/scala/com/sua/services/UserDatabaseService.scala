package com.sua.services

import com.sua.models.UserModel
import zio.{Has, Task, ZIO, ZLayer}

object UserDatabaseService {

  // 1. Service definition
  type UserDatabaseEnv = Has[UserDatabaseService.Service]

  trait Service {
    def insert(user: UserModel): Task[Unit]
  }

  // 2. Service implementation
  val live: ZLayer[Any, Nothing, UserDatabaseEnv] = ZLayer.succeed(new Service {
    override def insert(user: UserModel): Task[Unit] = Task {
      println(s"[Database] insert into public.user values ('${user.email}')")
    }
  })

  // 3. Front-facing API
  def insert(user: UserModel): ZIO[UserDatabaseEnv, Throwable, Unit] =
    ZIO.accessM(_.get.insert(user))
}

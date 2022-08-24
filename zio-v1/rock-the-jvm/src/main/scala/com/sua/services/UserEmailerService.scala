package com.sua.services

import com.sua.models.UserModel
import zio.{Has, Task, ZIO, ZLayer}

object UserEmailerService {

  // Service Structure Pattern
  // -------------------------
  // 1. Service definition
  // 2. Service implementation
  // 3. Front-facing API (TODO: Check the replace of accessM in last ZIO version)

  type UserEmailerEnv = Has[UserEmailerService.Service]

  // TODO: Check the Has[] replace
  // type UserEmailerEnv = Has[User, Service]
  // 1. Service definition
  trait Service {
    def notify(user: UserModel, message: String): Task[Unit] // ZIO[Any, Throwable, Unit]
  }

  // 2. Service implementation
  val live: ZLayer[Any, Nothing, UserEmailerEnv] = ZLayer.succeed(new Service {
    override def notify(user: UserModel, message: String): Task[Unit] = Task {
      println(s"[User emailer] Sending '$message' to ${user.email}")
    }
  })

  // 3. Front-facing API
  def notify(user: UserModel, message: String): ZIO[UserEmailerEnv, Throwable, Unit] =
  // TODO: Check the replace of accessM
    ZIO.accessM(hasService => hasService.get.notify(user, message))
}

// Conclusion
// ----------
// We went through an overview of ZIO and we covered the essence of ZLayer, enough to understand what it does and how it
// can help us build independent services, which we can plug together to create complex applications.

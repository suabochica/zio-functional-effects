package com.sua.services

import com.sua.models.UserModel
import zio._

object UserSubscriptionService {
  // Vertical Composition
  // --------------------

  // This kind of composition is more akin to regular function composition: the output of one ZLayer is the input of
  // another ZLayer, and the result becomes a new ZLayer with the input from the first and the output from the second.
  type UserSubscriptionEnv = Has[UserSubscriptionService.Service]

  // 1. Service definition
  class Service(notifier: UserEmailerService.Service, userDatabase: UserDatabaseService.Service) {
    def subscribe(user: UserModel): Task[UserModel] =
      for {
        _ <- userDatabase.insert(user)
        _ <- notifier.notify(user, s"Welcome ${user.name}! We have some nice ZIO content for you")
      } yield user
  }


  // 2. Service implementation
  // TODO: Check the replace of 'fromServices'
  val live: ZLayer[
    UserEmailerService.UserEmailerEnv with UserDatabaseService.UserDatabaseEnv,
    Nothing,
    UserSubscriptionEnv
  ] = ZLayer.fromServices[
    UserEmailerService.Service,
    UserDatabaseService.Service,
    UserSubscriptionService.Service
  ] {
    (userEmailer, userDatabase) => new Service(userEmailer, userDatabase)
  }

  // 3. Front-facing API
  def subscribe(user: UserModel): ZIO[UserSubscriptionEnv, Throwable, UserModel] =
    ZIO.accessM(_.get.subscribe(user))
}

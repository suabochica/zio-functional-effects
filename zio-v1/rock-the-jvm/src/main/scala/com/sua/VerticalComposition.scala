package com.sua

import com.sua.HorizontalComposition.userBackendLayer
import com.sua.models.UserModel
import com.sua.services.UserSubscriptionService
import com.sua.services.UserSubscriptionService.UserSubscriptionEnv
import zio._
import zio.console.Console

object VerticalComposition extends zio.App {

  // Vertical Composition
  // --------------------

  // This kind of composition is more akin to regular function composition: the output of one ZLayer is the input of
  // another ZLayer, and the result becomes a new ZLayer with the input from the first and the output from the second.
  val userSubscriptionLayer: ZLayer[Any, Nothing, UserSubscriptionEnv] = userBackendLayer >>> UserSubscriptionService.live

  val jim: UserModel = UserModel(s"Jim", "jim@theoffice.com")
  val message = "That's no true"

  override def run(args: List[String]): URIO[Any with Console, ExitCode] =
    UserSubscriptionService.subscribe(jim)
      .provideLayer(userSubscriptionLayer)
      .exitCode
}
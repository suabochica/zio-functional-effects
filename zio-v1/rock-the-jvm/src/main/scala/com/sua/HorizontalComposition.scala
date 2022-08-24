package com.sua

import com.sua.models.UserModel
import com.sua.services.UserDatabaseService.UserDatabaseEnv
import com.sua.services.UserEmailerService.UserEmailerEnv
import com.sua.services.{UserDatabaseService, UserEmailerService}
import zio._
import zio.console.Console

object HorizontalComposition extends zio.App {

  // Horizontal Composition
  // ----------------------

  // One way of combining ZLayers is the so-called “horizontal” composition. In a nutshell:

  // ZLayer[RIn1, Err1, ROut1] ++ ZLayer[RIn2, Err2, ROut2] =>
  //  ZLayer[RIn1 with RIn2, super(Err1, Err2), ROut1 with ROut2]

  // Here we can obtain a “bigger” ZLayer which can take as input RIn1 with RIn2, and produce as output ROut1 with
  // ROut2. If we suggested earlier that RIn is a “dependency”, then this new ZLayer combines (sums) the dependencies
  // of both ZLayers, and produces a “bigger” output, which can serve as dependency for a later ZLayer.

  // Let's combine UserEmailer with UserDatabase in the horizontal composition userBackendLayer.

  val userBackendLayer: ZLayer[Any, Nothing, UserDatabaseEnv with UserEmailerEnv] = {
    UserDatabaseService.live ++ UserEmailerService.live
  }

  val jim: UserModel = UserModel("Jim", "jim@theoffice.com")
  val message = "That's no true"

  override def run(args: List[String]): URIO[Any with Console, ExitCode] =
    UserEmailerService.notify(jim, message) // the kind of effect
      .provideLayer(userBackendLayer) // provide the input for that effect
      .exitCode
}

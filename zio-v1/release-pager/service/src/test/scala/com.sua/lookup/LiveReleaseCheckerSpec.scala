package com.sua.lookup

import zio.test.{DefaultRunnableSpec, ZSpec, assertCompletes}

object LiveReleaseCheckerSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[Environment, Failure] = suite("Live ReleaseCheckerSpec")(
    testM("Do not call services if there are no repositories") {
      ???
    },
    testM("Do not bother subscribers if there are no version updates") {
      ???
    },
    testM("Update repository version for the very first time") {
      ???
    },
    testM("Notify users abut new release") {
      ???
    },
    testM("GitHub client error should be handled") {
      ???
    }
  )

  private def refreshSchedule(
    gitHubClient: ULayer[GitHubClient],
    telegramClient: ULayer[telegramClient],
    subscriptionLogic: ULayer[SubscriptionLogic],
                             ): ZIO[ZEnv, Throwable, TestResult] = {
    val layer = (Logger.silent ++ gitHubClient ++ telegramClient ++ subscriptionLogic) >>> ReleaseChecker.live

    releaseChecker
      .refreshSchedule
      .provideLayer(layer)
      .as(assertCompletes)
  }
}
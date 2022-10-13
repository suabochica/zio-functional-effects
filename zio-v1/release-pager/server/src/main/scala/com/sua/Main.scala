package com.sua

// imports from domain
import com.sua.Config.DataBaseConfig
import com.sua.PagerError.{ ConfigurationError, MissingBotTokenError }
import com.sua.client.telegram.ChatId
import com.sua.subscription.Repository.{ Name, Version }

// imports from storage
import com.sua.subscription.chat.ChatStorage
import com.sua.subscription.repository.RepositoryVersionStorage

// imports from service
import com.sua.log.Logger
import com.sua.checker.ReleaseChecker
import com.sua.client.http.HttpClient
import com.sua.client.github.GitHubClient
import com.sua.client.telegram.TelegramClient
import com.sua.client.telegram.scenarios.CanoeScenarios
import com.sua.subscription.SubscriptionLogic
import com.sua.validation.RepositoryValidator

// imports from external libraries
import cats.effect.{ Blocker, Resource }
import canoe.api.{ TelegramClient => CanoeClient }

import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import pureconfig.ConfigSource

import scala.concurrent.duration.DurationInt
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext

import zio.blocking.Blocking
import zio.interop.catz.{
  catsIOResourceSyntax,
  taskConcurrentInstance,
  taskEffectInstance,
  zioContextShift
}
import zio.interop.Schedule
import zio.console.putStrLn
import zio.system.System
import zio.{
  system,
  App,
  ExitCode,
  IO,
  RIO,
  RManaged,
  Ref,
  Task,
  TaskManaged,
  UIO,
  ZEnv,
  ZIO,
  ZLayer
}

object Main extends App {
  private def getTelegramBotToken: ZIO[System, Object, String] =
    for {
      token <- system.env("BOT_TOKEN")
      token <- ZIO.fromOption(token).orElseFail(MissingBotTokenError)
    } yield token

  private def makeHttpClient: UIO[TaskManaged[Client[Task]]] =
    ZIO
      .runtime[Any]
      .map { implicit rts =>
        BlazeClientBuilder
          .apply[Task](platform.executor.asEC)
          .resource
          .toManaged
      }

  private def makeCanoeClient(
    token: String
  ): UIO[TaskManaged[CanoeClient[Task]]] =
    ZIO
      .runtime[Any]
      .map { implicit rts =>
        CanoeClient
          .global[Task](token)
          .toManaged
      }

  private def makeTransactor(
    config: DataBaseConfig
  ): RIO[Blocking, RManaged[Blocking, HikariTransactor[Task]]] = {
    def transactor(
      connectExecutionContext: ExecutionContext,
      transactExecutionContext: ExecutionContext
    ): Resource[Task, HikariTransactor[Task]] =
      HikariTransactor.newHikariTransactor[Task](
        config.driver,
        config.url,
        config.user,
        config.password,
        connectExecutionContext,
        Blocker.liftExecutionContext(transactExecutionContext)
      )

    ZIO.runtime[Blocking].map { implicit rt =>
      for {
        transactExecutionContext <-
          ZIO.access[Blocking](_.get.blockingExecutor.asEC).toManaged_
        transactor               <- transactor(
                                      rt.platform.executor.asEC,
                                      transactExecutionContext
                                    ).toManaged
      } yield transactor
    }
  }

  private def readConfig: IO[ConfigurationError, Config]             =
    ZIO
      .fromEither(ConfigSource.default.load[Config])
      .mapError(failures => ConfigurationError(failures.prettyPrint()))

  private def makeProgram(
    http4sClient: TaskManaged[Client[Task]],
    canoeClient: TaskManaged[CanoeClient[Task]]
  ): RIO[ZEnv, Long] = {
    val versionMap      =
      ZLayer.fromEffect(Ref.make(Map.empty[Name, Option[Version]]))
    val subscriptionMap =
      ZLayer.fromEffect(Ref.make(Map.empty[ChatId, Set[Name]]))
    val logger          = Logger.live

    val chatStorage              = subscriptionMap >>> ChatStorage.inMemory
    val repositoryVersionStorage =
      versionMap >>> RepositoryVersionStorage.inMemory
    val storage                  = chatStorage ++ repositoryVersionStorage

    val subscription = (logger ++ storage) >>> SubscriptionLogic.live

  }
  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    val program = for {
      token  <- getTelegramBotToken orElse UIO.succeed(
                  "972654063:AAEOiS2tpJkrPNsIMLI7glUUvNCjxpJ_2T8"
                )
      config <- readConfig
      _      <- FlywayMigration.migrate(config.releasePager.dataBaseConfig)

      http4sClient <- makeHttpClient
      canoeClient  <- makeCanoeClient(token)
      transactor   <- makeTransactor(config.releasePager.dataBaseConfig)

      _ <- makeProgram(http4sClient, canoeClient)
    } yield ()

    program
      .tapError(err => putStrLn(s"Execution failed with: ${err.getMessage}"))
      .exitCode

  }
}

package com.sua

// imports from domain
import com.sua.subscription.Repository.{ Name, Version }
import com.sua.Config.DataBaseConfig
import com.sua.PagerError.{ ConfigurationError, MissingBotTokenError }
import com.sua.subscription.repository.RepositoryVersionStorage

// imports from storage
import com.sua.subscription.chat.ChatStorage

// imports from service
import com.sua.log.Logger
import com.sua.client.telegram.ChatId
import com.sua.subscription.SubscriptionLogic

// imports from external libraries
import canoe.api.{ TelegramClient => CanoeClient }

import doobie.hikari.HikariTransactor
import doobie.util.transactor.Transactor

import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import zio.blocking.Blocking
import zio.interop.catz.{ catsIOResourceSyntax, taskEffectInstance }
import zio.console.putStrLn
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
  private def getTelegramBotToken: RIO[System, String] =
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
  ): RIO[Blocking, RManaged[Blocking, HikariTransactor[Task]]] =
    ???

  private def readConfig: IO[ConfigurationError, Config] =
    ???

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
      token <- getTelegramBotToken

      http4sClient <- makeHttpClient
      canoeClient  <- makeCanoeClient(token)

      _ <- makeProgram(http4sClient, canoeClient)
    } yield ()

    program
      .tapError(err => putStrLn(s"Execution failed with: ${err.getMessage}"))
      .exitCode

  }
}

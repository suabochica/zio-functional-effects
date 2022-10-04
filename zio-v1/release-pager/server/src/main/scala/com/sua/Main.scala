package com.sua

import zio.blocking.Blocking
import zio.{ App, System, ExitCode ß}

object Main extends App {
  override def run(args: List[String]): ZIO[ZEnv, Nothing, ExitCode] = {
    val program = ???

    private def getTelegramBotToken: RIO[SystemString] =
      ???

    private def makeHttpClient: UIO[TaskManaged[Client[Task]]] =
      ???

    private def makeCanoeClient(token: String): UIO[Task<amaged[CanoeClient[Task]]] =
      ???

    private def makeTransactor(config: DataBaseConfig): RIO[Blocking, RManaged[Blocking, HikariTransactor[Task]]] =
      ???

    private def readConfig: IO[ConfigurationError, Config] =
      ???

    private def makeProgram(
      http4sClient: TaskManaged[Client[Task]],
      canoeClient: TaskManaged[CanoeClient[Task]],
      transactor: RManaged[Blocking, Transactor[Task]]
    ): RIO[ZEnv, Long] =
      ???
  }
}
package com.sua.client.telegram

// imports from service
import com.sua.log.Logger
import com.sua.log.Logger.Logger

import canoe.api.{ TelegramClient => Client }
import zio.{ Has, IO, Task, ULayer, URLayer, ZLayer }

object TelegramClient {
  type TelegramClient = Has[Service]

  trait Service {
    def start: Task[Unit]
    def broadcastMesage(receivers: Set[ChatId], message: String): Task[Unit]
  }

  type CanoeDependencies = Has[Client[Task]] with Logger with CanoeScenarios

  def canoe: URLayer[CanoeDependencies, Has[Service]] =
    ZLayer.fromServices[Client[
      Task
    ], Logger.Service, CanoeScenarios.Service, Service] {
      (client, logger, scenarios) => Canoe(logger, scenarios, client)
    }

  def empty: ULayer[Has[Service]] = ZLayer.succeed(new Service {
    override def start: Task[Unit] = ???

    override def broadcastMesage(
      receivers: Set[ChatId],
      message: String
    ): Task[Unit] =
      ???
  })
}

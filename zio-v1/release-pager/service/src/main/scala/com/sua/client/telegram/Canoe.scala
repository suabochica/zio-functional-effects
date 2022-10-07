package com.sua.client.telegram

// imports from domain
// imports from service
import com.sua.log.Logger
import com.sua.log.Logger.Logger

// imports from external libraries
import canoe.api.models.ChatApi
import canoe.api.{ Bot, TelegramClient => Client }
import canoe.models.PrivateChat
import canoe.models.outgoing.TextContent

import zio.{ Task, ZIO }

final private[telegram] case class Canoe(
  logger: Logger,
  scenarios: CanoeScenarios,
  canoeClient: Client[Task]
) extends TelegramClient {
  implicit val canoe: Client[Task] = canoeClient

  def broadcastMessage(receivers: Set[ChatId], message: String): Task[Unit] =
    ZIO
      .foreach(receivers) { chatId =>
        val api = new ChatApi(PrivateChat(chatId.value, None, None, None))

        api.send(TextContent(message))
      }
      .unit

  override def start: Task[Unit] =
    logger.info("Starting Telegram polling") *>
      Bot
        .polling[Task]
        .follow(
          scenarios.start,
          scenarios.help,
          scenarios.add,
          scenarios.del,
          scenarios.list
        )
        .compile
        .drain
}

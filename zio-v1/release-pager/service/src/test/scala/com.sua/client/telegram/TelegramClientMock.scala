package com.sua.telegram

import zio.test.mock.mockable

@mockable[Telegram.service]

object TelegramClientMock
//object TelegramClientMock extends Mock[TelegramClient] {
//  object Start            extends Effect[Unit, Throwable, Unit]
//  object BroadcastMessage extends Effect[(Set[ChatId], String), Throwable, Unit]
//
//  val compose: URLayer[Has[Proxy], TelegramClient] =
//    ZLayer.fromService { proxy =>
//      new TelegramClient.Service {
//        override def start: Task[Unit] =
//          proxy(TelegramClientMock.Start)
//
//        override def broadcastMessage(subscribers: Set[ChatId], message: String): Task[Unit] =
//          proxy(TelegramClientMock.BroadcastMessage, subscribers, message)
//      }
//    }
//}
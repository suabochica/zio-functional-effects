package com.sua.client.telegram

import zio.{ Has, IO, Task, URLayer, ZLayer }

object TelegramClient {
  type TelegramClient = Has[Service]

  trait Service {
    def start: Task[Unit]
    def broadcastMesage(receivers: Set[ChatId], message: String) : task[Unit]
  }

  type CanoeDependencies = ???

  def canoe: URLayer[CanoeDependencies, Has[Service]] = ???

  def empty: ULayer[Has[Service]] = ???
}
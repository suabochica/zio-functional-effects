package com.sua.subscription

object Subscription {
  type Subscription = Has[Service]

  trait Service {
    def subscribe(chatId: ChatId, name: Name): Task[Unit]
    def unsubscribe(chatId: ChatId, name: Name): Task[Unit]
    def listSubscriptions(chatId: ChatId): Task[Set[Name]]
    def listSubscribers(name: Name): Task[Set[ChatId]]
    def updateVersions(updateVersions: Map[Name, Version]): Task[Unit]
  }

  type LiveDependencies = ???

  def live: URLayer[LiveDependencies, Has[Service]] = ???
}
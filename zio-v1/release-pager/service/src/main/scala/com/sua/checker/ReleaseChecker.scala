package com.sua.checker

object ReleaseChecker {
  type ReleaseChecker = Has[Service]

  trait Service {
    def scheduleRefresh: Task[Unit]
  }

  type LiveDependencies = ???

  def live: URLayer[LiveDependencies, Has[Service]] =
    ???
}
package com.sua.log

import zio.clock.Clock
import zio.console.{ Console => ConsoleZIO }
import zio.{ Has, UIO, ULayer, URLayer, ZLayer }

object Logger {
  type Logger = Has[Service]

  trait Service {
    def trace(message: => String): UIO[Unit]
    def debug(message: => String): UIO[Unit]
    def info(message: => String): UIO[Unit]
    def warn(message: => String): UIO[Unit]
    def error(message: => String): UIO[Unit]
    def error(throwable: Throwable)(message: => String): UIO[Unit]
  }

  def live: URLayer[Clock with ConsoleZIO, Has[Service]] =
    ZLayer.fromServices[Clock.Service, ConsoleZIO.Service, Service] {
      (clock, console) => LoggerLive(clock, console)
    }

  def silent: ULayer[Logger] =
    ZLayer.succeed(Silent)
}

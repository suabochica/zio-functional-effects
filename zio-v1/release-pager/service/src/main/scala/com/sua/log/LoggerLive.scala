package com.sua.client.log

// imports from domain
import com.sua.ThrowableOps.ThrowableOps

// imports from services
import com.sua.log.Logger

import zio.clock.Clock
import zio.console.{ Console => ConsoleZIO }
import zio.{ Has, UIO, ULayer, URLayer, ZLayer }

final private[log] case class LoggerLive(
  clock: Clock.Service,
  console: ConsoleZIO.Service
) extends Logger.Service {
  def trace(message: => String): UIO[Unit] = print(message)
  def debug(message: => String): UIO[Unit] = print(message)
  def info(message: => String): UIO[Unit]  = print(message)
  def warn(message: => String): UIO[Unit]  = print(message)
  def error(message: => String): UIO[Unit] = print(message)

  def error(throwable: ThrowableOps)(message: => String): UIO[Unit] =
    for {
      _ <- print(message)
      _ <- console.putStrLn(throwable.stackTrace).orDie
    } yield ()

  private def print(message: => String): UIO[Unit] =
    (for {
      timestamp <- clock.currentDateTime
      _         <- console.putStrLn(s"[$timestamp] $message")
    } yield ()).orDie
}

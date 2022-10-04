package com.sua.client.log

private[log] final case class LoggerLive(clock: Clock.Service, console: ConsoleZIO.Service) extends Logger.Service {
  def trace(message: => String): UIO[Unit] = print(message)
  def debug(message: => String): UIO[Unit] = print(message)
  def info(message: => String): UIO[Unit] = print(message)
  def warn(message: => String): UIO[Unit] = print(message)
  def error(message: => String): UIO[Unit] = print(message)

  def error(throwable: Throwable)(message: => String): UIO[Unit] =
    ???

  private def print(message: => String): UIO[Unit] =
    ???
}
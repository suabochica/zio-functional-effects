package com.sua.log

import zio.{ Has, IO, Task, URLayer, ZLayer }

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
    ???

  def silent: ULayer[Logger] =
    ???
}
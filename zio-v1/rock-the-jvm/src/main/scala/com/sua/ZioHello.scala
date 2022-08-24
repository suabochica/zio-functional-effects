package com.sua

import zio.{ExitCode, ZIO}
import zio.console._

import java.io.IOException

object ZioHello extends zio.App {
  val greeting: ZIO[Console, IOException, Unit] = for {
    _ <- putStrLn("Hi, what is your name")
    name <- getStrLn
    _ <- putStrLn(s"Hello $name, welcome to ZIO")
  } yield ()

  override def run(args: List[String]): zio.URIO[Console, ExitCode] =
    greeting.exitCode
}
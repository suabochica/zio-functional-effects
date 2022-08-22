package com.zio.sua

import zio.ZIO
import zio.console._

object ZioHello extends zio.App {
  val meaningOfLife = ZIO.succeed(42)
  val aFailure = ZIO.fail("Something went wrong")

  val greeting = for {
    _ <- putStrLn("Hi, what is your name")
    name <- getStrLn
    _ <- putStrLn(s"Hello $name, welcome to ZIO")
  } yield ()

  override def run(args: List[String]) =
    greeting.exitCode
}
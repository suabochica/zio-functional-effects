package com.sua.chapter02

import zio.{App, ExitCode, URIO, ZIO}
import zio.console.{getStrLn, putStrLn, Console}

import java.io.IOException

/** Using the `Console`, write a little program that asks the user what their
  * name is, and then prints it out to them with a greeting.
  */

object e17HelloHuman extends App {
  def run(args: List[String]): URIO[Console, ExitCode] =
    helloHuman.exitCode

  val helloHuman: ZIO[Console, IOException, Unit] =
    for {
      _    <- putStrLn("What is your name?")
      name <- getStrLn
      _    <- putStrLn("Hello, " + name)
    } yield ()
}

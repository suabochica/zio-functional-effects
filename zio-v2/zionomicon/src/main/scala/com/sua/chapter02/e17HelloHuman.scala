package com.sua.chapter02

import zio.{ Console, ZIO }

/**
 *  Using the `Console`, write a program that ask the user what their name is
 *  and then prints it out to the with a greeting
 */

object e17HelloHuman {
  object HelloHuman extends ZIOAppDefault {
    val run =
      for {
        _ <- Console.printLine("What is your name?")
        name <- Console.readLine
        _ <- Console.printLine("Hello, " + name)
      } yield ()
  }
}
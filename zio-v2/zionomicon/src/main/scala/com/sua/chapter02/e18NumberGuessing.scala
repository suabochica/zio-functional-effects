package com.sua.chapter02

import zio.{ Console, Random, ZIO }

/**
 * Using the `Console` and `Random` services in ZIO, write a program that asks
 * the user to guess a randomly chosen number between 1 and 3, and
 * prints out if they were correct or not
 */

object e18NumberGuessing extends ZIOAppDefault {
  val run =
    for {
      int <- Random.nextIntBounded(2).map(_ + 1)
      _ <- Console.printLine("Guess a number from 1 to 3:")
      num <- Console.readLine
      _ <- if (num == int.toString) Console.printLine("You guessed right!")
      else Console.printLine(s"You guessed wrong, the number was $int!")
    } yield ()
}

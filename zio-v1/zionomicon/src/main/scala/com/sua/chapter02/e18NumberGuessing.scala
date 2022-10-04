package com.sua.chapter02

import zio.{App, ExitCode, URIO, ZIO}
import zio.console.{getStrLn, putStrLn, Console}
import zio.random.{nextIntBounded, Random}

/** Using the `Console` and `Random` services in ZIO, write a little program
  * that asks the user to guess a randomly chosen number between 1 and 3, and
  * prints out if they were correct or not.
  */
object e18NumberGuessing extends App {
  def run(args: List[String]): URIO[Console with Random, ExitCode] =
    numberGuessing.exitCode

  val numberGuessing =
    for {
      int <- nextIntBounded(2).map(_ + 1)
      _   <- putStrLn("Guess a number from 1 to 3:")
      num <- getStrLn
      _ <-
        if (num == int.toString) putStrLn("You guessed right!")
        else putStrLn(s"you guessed wrong, the number was $int!")
    } yield ()
}

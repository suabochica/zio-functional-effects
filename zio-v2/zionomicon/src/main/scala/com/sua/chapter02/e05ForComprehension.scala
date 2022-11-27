package com.sua.chapter02

import scala.util.Random
import scala.io.StdIn
import zio.{Task, ZIO}

/**
 * Rewrite the following ZIO code that uses `flatMap` into a
 * _for comprehension_.
 */
object e05ForComprehension {
  val random: Task[Int] = ZIO.attempt(Random.nextInt(3) + 1)
  val readLine: Task[String] = ZIO.attempt(StdIn.readLine())
  def printLine(line: String): Task[Unit] = ZIO.attempt(println(line))

  private def checkNumber(randomNumber: String, numberToGuess: String) = {
    if (randomNumber != numberToGuess) {
      printLine(s"You guessed wrong, the number was $randomNumber!")
    } else printLine("You guessed right!")
  }

  for {
    randomNumber <- random
    _   <- printLine("Guess a number from 1 to 3:")
    numberToGuess <- readLine
    _ <- checkNumber(randomNumber.toString, numberToGuess)
  } yield ()
}

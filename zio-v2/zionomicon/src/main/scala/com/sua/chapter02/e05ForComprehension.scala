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
  def printLine(line: String): Task[Unit] = ZIO.attempt(println(line))
  val readLine: Task[String] = ZIO.attempt(StdIn.readLine())

  for {
    int <- random
    _   <- printLine("Guess a number from 1 to 3:")
    num <- readLine
    _ <- if (num == int.toString) printLine("You guessed right!")
         else printLine(s"You guessed wrong, the number was $int!")
  } yield ()
}

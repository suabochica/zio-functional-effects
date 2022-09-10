package com.sua.chapter02

import scala.util.Random
import scala.io.StdIn
import zio.{Task, ZIO}

/**
 * Rewrite the following ZIO code that uses flatMap into a for comprehension
 */

object e05ForComprehension {
  val random: Task[Int] = ZIO.effect(Random.nextInt(3) + 1)
  val readLine: Task[String] = ZIO.effect(StdIn.readLine())

  def printLine(line: String): Task[Unit] = ZIO.effect(println(line))

  // rn: random number
  random.flatMap(rn =>
    printLine("Guess a number from 1 to 3: ")
      .flatMap(_ =>
        readLine
          .flatMap(num =>
            if (num == rn.toString)
              printLine("You guessed!")
            else
              printLine(s"You guessed wrong, the number was ${rn}")
          )
      )
  )

  // for comprehension version
  // --------------------------

  private def checkNumber(randomNumber: String, numberToGuess: String) =
    if (randomNumber != numberToGuess) {
      printLine(s"You guessed wrong, the number was ${randomNumber}")
    } else printLine("You guessed!")

  for {
    randomNumber  <- random
    _             <- printLine("Guess a number from 1 to 3: ")
    numberToGuess <- readLine
    _             <- checkNumber(randomNumber.toString, numberToGuess)
  } yield ()
}

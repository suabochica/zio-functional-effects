package com.sua.chapter02

import zio.{Task, ZIO}

import scala.io.StdIn.readLine

/**
 * Rewrite the following ZIO code that uses flatMap into a for comprehension
 */

object e04FlatMap {
  def printLine(line: String): Task[Unit] = ZIO.effect(println(line))

  val readLineZio: Task[String] = ZIO.effect(readLine)

  printLine("What is your name?")
    .flatMap(
      _ => readLineZio.flatMap(name => printLine(s"Hello ${name}"))
    )
}

object e04FlatMapOperator {
  def printLine(line: String): Task[Unit] = ZIO.effect(println(line))

  val readLineZio: Task[String] = ZIO.effect(readLine)

  printLine("What is your name?") *> readLineZio.flatMap(name => printLine(s"Hello ${name}"))
}

object e04ForComprehension {
  def printLine(line: String): Task[Unit] = ZIO.effect(println(line))

  val readLineZio: Task[String] = ZIO.effect(readLine)

  for {
    _     <- printLine("What is your name?")
    name  <- readLineZio
    _     <- printLine(s"Hello, ${name}!")
  } yield ()
}

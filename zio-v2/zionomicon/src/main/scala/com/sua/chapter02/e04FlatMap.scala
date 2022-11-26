package com.sua.chapter02

import zio.{Task, ZIO}

import scala.io.StdIn.readLine

/**
 * Rewrite the following ZIO code that uses `flatMap` into a
 * _for comprehension_.
 */

object e04FlatMap {
  def printLine(line: String): Task[Unit] = ZIO.attempt(println(line))
  val readLineZIO: Task[String] = ZIO.attempt(readLine())

  for {
    _ <- printLine("What is your name?")
    name <- readLineZIO
    _ <- printLine(s"Hello, $name")
  } yield ()
}
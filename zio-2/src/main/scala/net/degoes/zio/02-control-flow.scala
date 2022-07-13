package net.degoes.zio

import zio._
import scala.collection.immutable.Nil
import scala.annotation.tailrec

/**
 * ZIO is high biased to functional programming, so for looping it uses recursion.
 * Every time you use recursion you will lead on `flatMap`.
 *
 * Word in the street is that while loops tend to be more efficient that recursion (even tail recursion)
 */

object Looping extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Implement a `repeat` combinator using `flatMap` (or `zipRight`) and recursion.
   */
  def repeat[R, E, A](n: Int)(effect: ZIO[R, E, A]): ZIO[R, E, Chunk[A]] = {
    if (n <= 0) ZIO.succeed(Chunk.empty)

    /**
     *  else {
     *  val one = effect.map(Chunk(_))
     *  val others = repeat(n - 1)(effect)
     *
     *  for {
     *  left <- one
     *  right <- others
     *  } yield left ++ right
     *  }
     */
    else effect.zipWith(repeat(n - 1)(effect))((a, as) => Chunk(a) ++ as)
  }

  def repeatRef[R, E, A](n: Int)(effect: ZIO[R, E, A]): ZIO[R, E, Chunk[A]] = {
    def repeat1(result: Ref[Chunk[A]], reminder: Ref[Int]) =
      for {
        n <- reminder.get
        _ <- if (n <= 0) ZIO.unit
        else
          for {
            a <- effect
            _ <- reminder.update(_ - 1)
            _ <- result.update(_ :+ a)
          } yield ()
      } yield ()

    for {
      result  <- Ref.make(Chunk.empty[A])
      ref     <- Ref.make(n)
      _       <- repeat1(result, ref).repeatWhileZIO(_ => ref.get.map(_ > 0))
      chunk   <- result.get
    } yield  chunk
  }

  def repeatCollect[R, E, A](n: Int)(effect: ZIO[R, E, A]): ZIO[R, Nothing, Chunk[Either[E, A]]] =
      repeat(n)(effect.either)

  val run =
    repeat(100)(Console.printLine("All work and no play makes Jack a dull boy"))
}

object Interview extends ZIOAppDefault {
  import java.io.IOException

  val questions =
    "Where where you born?" ::
      "What color are your eyes?" ::
      "What is your favorite movie?" ::
      "What is your favorite number?" :: Nil

  /**
   * EXERCISE
   *
   * Implement the `getAllAnswers` function in such a fashion that it will ask
   * the user each question and collect them all into a list.
   */
  def getAllAnswers(questions: List[String]): ZIO[Any, IOException, List[String]] =
    questions match {
      case Nil     => ZIO.succeed(List.empty)
      case q :: qs =>
        for {
          _  <- Console.printLine(q)
          a  <- Console.readLine("> ")
          as <- getAllAnswers(qs)
        } yield a :: as
    }

  /**
   * EXERCISE
   *
   * Use the preceding `getAllAnswers` function, together with the predefined
   * `questions`, to ask the user a bunch of questions, and print the answers.
   */
  val run =
    getAllAnswers(questions)
}

object InterviewGeneric extends ZIOAppDefault {
  import java.io.IOException

  val questions =
    "Where where you born?" ::
      "What color are your eyes?" ::
      "What is your favorite movie?" ::
      "What is your favorite number?" :: Nil

  /**
   * EXERCISE
   *
   * Implement the `iterateAndCollect` function.
   */
  def iterateAndCollect[R, E, A, B](as: List[A])(f: A => ZIO[R, E, B]): ZIO[R, E, List[B]] =
    as match {
      case Nil     => ZIO.succeed(Nil)
      case a :: as =>
        for {
          b <- f(a)
          bs <- iterateAndCollect(as)(f)
        }  yield b :: bs
    }

  val run =
    iterateAndCollect(questions) { question =>
      for {
        _ <- Console.printLine(question)
        answer <- Console.readLine("> ")
      } yield answer
    }
}

object InterviewForeach extends ZIOAppDefault {

  val questions =
    "Where where you born?" ::
      "What color are your eyes?" ::
      "What is your favorite movie?" ::
      "What is your favorite number?" :: Nil

  /**
   * EXERCISE
   *
   * Using `ZIO.foreach`, iterate over each question in `questions`, print the
   * question to the user (`Console.printLine`), read the answer from the user
   * (`Console.readLine`), and collect all answers into a collection. Finally, print
   * out the contents of the collection.
   */
  val run =
    ZIO.foreach(questions) { question =>
      for {
        _ <- Console.printLine(question)
        answer <- Console.readLine("> ")
      } yield answer
    }

}

object WhileLoop extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Implement the functional effect version of a while loop so the
   * application runs correctly.
   */
  def whileLoop[R, E, A](cond: UIO[Boolean])(zio: ZIO[R, E, A]): ZIO[R, E, Chunk[A]] =
    ???
    /*
  for {
      continue  <- cond
      chunk     <- if (continue) zio.zipWith(whileLoop(cond)(zio))(_ +: _)
                  else ZIO.succeed(Chunk.empty)
    } yield ()
    */

  val run = {
    def loop(variable: Ref[Int]) =
      whileLoop(variable.get.map(_ < 100)) {
        for {
          value <- variable.get
          _     <- Console.printLine(s"At iteration: ${value}")
          _     <- variable.update(_ + 1)
        } yield ()
      }

    (for {
      variable <- Ref.make(0)
      _        <- loop(variable)
    } yield 0)
  }
}

object Iterate extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Implement the `iterate` function such that it iterates until the condition
   * evaluates to false, returning the "last" value of type `A`.
   */
  def iterate[R, E, A](start: A)(cond: A => Boolean)(f: A => ZIO[R, E, A]): ZIO[R, E, A] =
    if (cond(start)) f(start).flatMap(iterate(_)(cond)(f))
    else ZIO.succeed(start)

  val run =
    iterate(0)(_ < 100) { i =>
      Console.printLine(s"At iteration: ${i}").as(i + 1)
    }
}

object TailRecursive extends ZIOAppDefault {
  trait Response
  trait Request {
    def returnResponse(response: Response): Task[Unit]
  }

  lazy val acceptRequest: Task[Request] = ZIO.attempt(new Request {
    def returnResponse(response: Response): Task[Unit] =
      ZIO.attempt(println(s"Returning response ${response}"))
  })

  def handleRequest(request: Request): Task[Response] = ZIO.attempt {
    println(s"Handling request ${request}")
    new Response {}
  }

  /**
   * EXERCISE
   *
   * Make this infinite loop (which represents a webserver) effectfully tail
   * recursive.
   *
   * Problem: We have a infinite loop that will exhaust the memory of your machine
   * Solution: Tail recursion to keep stack safety
   *
   * If you are exposed to infinite loops, not use for comprehension
   *
   * Rule:
   * - Use `ref` when
   * - Use `forever` when
   *
   * you can use "@tailrec" annotation to indicate to scala compiler
   * that your method should be tail recursive.
   */

  /*
  lazy val webserver: Task[Nothing] =
    for {
      request  <- acceptRequest
      response <- handleRequest(request)
      _        <- request.returnResponse(response)
      nothing  <- webserver
    } yield nothing
  */

  // Alternative using the `.forever` ZIO combinator.
  lazy val webserver: Task[Nothing] =
    (for {
      request  <- acceptRequest
      response <- handleRequest(request)
      _        <- request.returnResponse(response)
    } yield ()).forever

  /* Scala translation (not tail recursion)
  lazy val webserver2: Task[Nothing] =
    acceptRequest.flatMap(request =>
      handleRequest(request).flatMap(response =>
      request.returnResponse(response).flatMap(_ =>
        webserver2.map(nothing => nothing) // here is the infinite loop
      )
    )
  )
  */

  // Scala translation (tail recursive) Remove the final `.map`
  lazy val webserver2: Task[Nothing] =
    acceptRequest.flatMap(request =>
      handleRequest(request).flatMap(response =>
        request.returnResponse(response).flatMap(_ =>
          webserver2
        )
      )
    )

  // Not tail recursive fibonacci
  def fib(n: Int): Int =
    if (n <= 1) n
    else fib(n - 1) + fib(n - 2)

  val run =
    (for {
      fiber <- webserver.fork
      _     <- ZIO.sleep(100.millis)
      _     <- fiber.interrupt
    } yield ())
}

package net.degoes.zio

import zio._

/*
 * INTRODUCTION
 *
 * ZIO effects are immutable data values that model a possibly complex series
 * of async, concurrent, resourceful, and contextual computations.
 *
 * The only effect type in ZIO is called ZIO, and has three type parameters,
 * which permit accessing context from an environment (`R`), failing with a
 * value of a certain type (`E`), and succeeding with a value of a certain
 * type (`A`).
 *
 * Unlike Scala's Future, ZIO effects are completely lazy. All methods on ZIO
 * effects return new ZIO effects. No part of the workflow is executed until
 * one of the `unsafeRun*` functions are called.
 *
 * ZIO effects are transformed and combined using methods on the ZIO data type.
 * For example, two effects can be combined into a sequential workflow using
 * an operator called `zip`. Similarly, two effects can be combined into a
 * parallel workflow using an operator called `zipPar`.
 *
 * The operators on the ZIO data type allow very powerful, expressive, and
 * type-safe transformation and composition, while the methods in the ZIO
 * companion object allow building new effects from simple values (which are
 * not themselves effects).
 *
 * In this section, you will explore both the ZIO data model itself, as well
 * as the very basic operators used to transform and combine ZIO effects, as
 * well as a few simple ways to build effects.
 */

/**
 * A good mental model for ZIO[R, E, A] is:
 * {{{
 *   ZEnvironment[R] => Either[E, A]
 * }}}
 * This can be interpreted as a function which, given a ZIO environment
 * (which is a map that contain classes of different types), a ZIO
 * returns either a failure of type E or a success of type A.
 */
object ZIOModel {
  // (success: => A) Binding parameter, defer evaluation of the expression
  // Biding parameter != Lazy parameter
  // Eager evaluation
  //
  // def add(l: Int, r: Int) = l + r

  // Bundle to function zero
  // add(2 + ((throw new Error): Int), 4 + 1)

  // In procedural programming you compose effect different

  /**
   * EXERCISE
   *
   * Implement all missing methods on the ZIO companion object.
   */
  object ZIO {
    def succeed[A](success: => A): ZIO[Any, Nothing, A] =
      ZIO(_ => Right(success))

    def fail[E](error: => E): ZIO[Any, E, Nothing] =
      ZIO(_ => Left(error))

    def attempt[A](code: => A): ZIO[Any, Throwable, A] =
      ZIO(_ => try Right(code) catch { case t: Throwable => Left(t)})

    def environment[R]: ZIO[R, Nothing, ZEnvironment[R]] =
      ZIO(env => Right(env))
  }

  /**
   * EXERCISE
   *
   * Implement all missing methods on the ZIO class.
   */
  final case class ZIO[-R, +E, +A](run: ZEnvironment[R] => Either[E, A]) { self =>
    def map[B](f: A => B): ZIO[R, E, B] =
      ZIO(env => self.run(env).map(f))

    // Execute a success case in sequence
    def flatMap[R1 <: R, E1 >: E, B](f: A => ZIO[R1, E1, B]): ZIO[R1, E1, B] =
      ZIO(env => self.run(env).flatMap(a => f(a).run(env)))

    // Sequential composition of two effects
    def zip[R1 <: R, E1 >: E, B](that: ZIO[R1, E1, B]): ZIO[R1, E1, (A, B)] =
      self.flatMap(a => that.map(b => (a, b)))

    def either: ZIO[R, Nothing, Either[E, A]] =
      ZIO(env => Right(self.run(env)))

    def provideEnvironment(r: ZEnvironment[R]): ZIO[Any, E, A] =
      ZIO(_ => self.run(r))

    def orDie(implicit ev: E <:< Throwable): ZIO[R, Nothing, A] =
      ZIO(r => self.run(r).fold(throw _, Right(_)))
  }

  def printLine(line: String): ZIO[Any, Nothing, Unit] =
    ZIO.attempt(println(line)).orDie

  // The difference between 'def' (method, every time you call it, it returns a value)
  // and 'val (value)' is that `val` is more efficient. How ever, under the context
  // of ZIO ther is no difference between `def` and `val`
  // ordinary value that store it once in time.
  val readLine: ZIO[Any, Nothing, String] =
    ZIO.attempt(scala.io.StdIn.readLine()).orDie

  def run[A](zio: ZIO[Any, Throwable, A])(implicit unsafe: Unsafe): A =
    zio.run(ZEnvironment.empty).fold(throw _, a => a)

  /**
   * Run the following main function and compare the results with your
   * expectations.
   */
  def main(args: Array[String]): Unit = {
    // Scala 3 got contextual function, so the Unsafe use is cleaner
    Unsafe.unsafe { implicit u =>
      run {
        printLine("Hello, what is your name?").flatMap(
          _ => readLine.flatMap(name => printLine(s"Your name is: ${name}"))
        )
      }
    }
  }
}

object ZIOTypes {
  type ??? = Nothing
  // ZiO[Environment, Error, Success]
  // Don't need environment: Any
  // Don't need error: Nothing
  // Don't need success: Nothing
  // Need ordinary exceptions: Throwable for Error

  /**
   * EXERCISE
   *
   * Provide definitions for the ZIO type aliases below. (Goal: Reduce typing)
   */
  type Task[+A]     = ZIO[Any, Throwable, A]
  type UIO[+A]      = ZIO[Any, Nothing, A]
  type RIO[-R, +A]  = ZIO[R, Throwable, A]
  type IO[+E, +A]   = ZIO[Any, E, A]
  type URIO[-R, +A] = ZIO[R, Nothing, A]
}

// Create your own main function extending ZIPAppDefault. It bundle the
// runtime system for ZIO. Stand in ZIO as far you can
object SuccessEffect extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `ZIO.succeed`, create an effect that succeeds with the string
   * "Hello World".
   */
  val run = {
    getArgs.flatMap(args => Console.print(args))
  }
}

object HelloWorld extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Implement a simple "Hello World!" program by invoking `Console.printLine`
   * to create an effect that, when executed, will print out "Hello World!" to
   * the console.
   */
  val run =
    Console.printLine("Hello World!")
}

object SimpleMap extends ZIOAppDefault {
  import Console.readLine

  /**
   * EXERCISE
   *
   * Using `ZIO#map`, map the string success value of `Console.readLine` into an
   * integer (the length of the string)`.
   */
  val run =
    Console.readLine.map(line => line.length)
}

object PrintSequenceZip extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `zip`, compose a sequence of `Console.printLine` effects to produce an effect
   * that prints three lines of text to the console.
   */
  val run =
    Console.printLine("Line 1")
      .zip(Console.printLine("Line 2"))
      .zip(Console.printLine("Line 3"))
}

object PrintSequence extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * a.zip(b)       alias <*> // (a, b)
   * a.zipRight(b)  alias *>  // b
   * a.zipLeft(b)   alias <*  // a
   *
   * Using `*>` (`zipRight`), compose a sequence of `Console.printLine` effects to
   * produce an effect that prints three lines of text to the console.
   */
  val run =
    Console.printLine("Line 1" ) *>
    Console.printLine("Line 2" ) *>
    Console.printLine("Line 3" )
}

object PrintReadSequence extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `*>` (`zipRight`), sequentially compose a `Console.printLine` effect, which
   * models printing out "Hit Enter to exit...", together with a `Console.readLine`
   * effect, which models reading a line of text from the console.
   */
  val run =
    Console.printLine("Hit Enter to exit..." ) *>
    Console.readLine
}

object SimpleDuplication extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Refactoring is something we should do ofter for code healthy
   * Small incremental transformation under behavior preserving.
   * DRY - Don't Repeat Yourself
   *
   * In the following program, the expression `Console.printLine("Hello again")`
   * appears three times. Factor out this duplication by introducing a new
   * value that stores the expression, and then referencing that variable
   * three times.
   */
  val run = {
    val helloAgain = Console.printLine("Hello again")

    /**
    Console.printLine("Hello") *>
    Console.printLine("Hello again") *>
    Console.printLine("Hello again") *>
    Console.printLine("Hello again")
    */

    /**
    Console.printLine("Hello") *>
      helloAgain *>
      helloAgain *>
      helloAgain
    */

    Console.printLine("Hello") *>
      helloAgain.repeatN(2)
  }
}

object FlatMap extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * The following program is intended to ask the user for their name, then
   * read their name, then print their name back out to the user. However,
   * the `zipRight` (`*>`) operator is not powerful enough to solve this
   * problem, because it does not allow a _subsequent_ effect to depend
   * on the success value produced by a _preceding_ effect.
   *
   * `zip` is not enough because the effect of the the right is not aware
   * of the effect in the left.
   *
   * Solve this problem by using the `ZIO#flatMap` operator, which composes
   * a first effect together with a "callback", which can return a second
   * effect that depends on the success value produced by the first effect.
   *
   * Remember that flatMap is a success handler.
   *
   */
  val run = {
    /**
    Console.printLine("What is your name?") *>
      Console.readLine *> // Use .flatMap(...) here
      Console.printLine("Your name is: ")
    */

    Console.printLine("What is your name?") *>
      Console.readLine
        .flatMap(name => Console.printLine(s"Your name is: ${name}"))
  }
}

object PromptName extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * The following program uses a combination of `zipRight` (`*>`), and
   * `flatMap`. However, this makes the structure of the program harder
   * to understand. Replace all `zipRight` by `flatMap`, by ignoring the
   * success value of the left hand effect.
   */
  val run =
    /**
    Console.printLine("What is your name?") *>
      Console.readLine.flatMap(name => Console.printLine(s"Your name is: ${name}"))
    */
    Console.printLine("What is your name?")
      .flatMap(_ => Console.readLine.
        flatMap(name => Console.printLine(s"Your name is: ${name}"))
      )

  /**
   * EXERCISE
   *
   * Implement a generic "zipRight" that sequentially composes the two effects
   * using `flatMap`, but which succeeds with the success value of the effect
   * on the right-hand side.
   */
  def myZipRight[R, E, A, B](
    left: ZIO[R, E, A],
    right: ZIO[R, E, B]
  ): ZIO[R, E, B] =
    left.flatMap(_ => right)
}

object ForComprehension extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * For comprehension is syntactic sugar for flatMap operations.
   * below it is share his structure:
   *
   * for {
   *  a <- z
   *  _ <- y
   *  c <- f(a)
   * } yield a + c
   *
   * Rewrite the following program to use a `for` comprehension.
   */
  val run = {
    /**
    Console
      .printLine("What is your name?")
      .flatMap(
        _ => Console.readLine.flatMap(name => Console.printLine(s"Your name is: ${name}"))
      )
    */

    for {
      _     <- Console.printLine("What is your name?")
      name  <- Console.readLine
      _     <- Console.printLine(s"Your name is: ${name}")
    } yield  ()

    /**
     * What Scala compiler sees:
    Console.printLine("What is your name?")
      .flatMap(_ => Console.readLine
        .flatMap(name => Console.printLine(s"Your name is: ${name}")
          .map(_ => ())
        )
      )
    */
  }
}

object ForComprehensionBackward extends ZIOAppDefault {

  val readInt = Console.readLine.flatMap(string => ZIO.attempt(string.toInt)).orDie

  /**
   * EXERCISE
   *
   * Rewrite the following program, which uses a `for` comprehension, to use
   * explicit `flatMap` and `map` methods. Note: each line of the `for`
   * comprehension will translate to a `flatMap`, except the final line,
   * which will translate to a `map`.
   *
   * Guide: the number of flatMap is the total of the storage instructions
   * minus one.
   */
  val run = {
    for {
      _   <- Console.printLine("How old are you?")
      age <- readInt
      _ <- if (age < 18) Console.printLine("You are a kid!")
          else Console.printLine("You are all grown up!")
    } yield ()

    /**
     * What Scala compiler sees:
    Console.printLine("How old are you?")
      .flatMap(age => readInt
        .flatMap(_ => if (age < 18) Console.printLine(s"You are a kid") else  Console.printLine(s"You are all grown up!")
          .map(_ => ())
        )
      )
     */
  }
}

object NumberGuesser extends ZIOAppDefault {
  def analyzeAnswer(random: Int, guess: String) =
    if (random.toString == guess.trim) Console.printLine("You guessed correctly!")
    else Console.printLine(s"You did not guess correctly. The answer was ${random}")

  /**
   * EXERCISE
   *
   * Choose a random number (using `Random.nextInt`), and then ask the user to guess
   * the number (using `Console.readLine`), feeding their response to `analyzeAnswer`,
   * above.
   *
   * Whe you use conditional remember always return a ZIO effect for both branch in
   * the condition (i.e. if (condition) [ZIO effect] is not valid)
   */
  val run =
    for {
      rand <- Random.nextIntBetween(1, 11)
      _ <- Console.printLine("Enter a number between 1 and 10;")
      guess <- Console.readLine
      _ <- analyzeAnswer(rand, guess)
    } yield ()
}

object SingleSyncInterop extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using ZIO.attempt, convert `println` into a ZIO function.
   * attempt is used to synchronous APIs
   *
   * ZIO.attempt do two things
   * 1. Try two catch your code and handle the success or failure scenarios
   * 2. Turns the statement to eager code in value oriented code.
   */
  def myPrintLn(line: String): Task[Unit] = ZIO.attempt(println(line))

  val run =
    myPrintLn("Hello World!")
}

object MultipleSyncInterop extends ZIOAppDefault {

  /**
   * Using `ZIO.attempt`, wrap Scala's `println` method to lazily convert it
   * into a functional effect, which describes the action of printing a line
   * of text to the console, but which does not actually perform the print.
   */
  def printLine(line: String): Task[Unit] = ZIO.attempt(println(line))

  /**
   * Using `ZIO.attempt`, wrap Scala's `scala.io.StdIn.readLine()` method to
   * lazily convert it into a ZIO effect, which describes the action of
   * printing a line of text to the console, but which does not actually
   * perform the print.
   */
  val readLine: Task[String] = ZIO.attempt(scala.io.StdIn.readLine())

  val run = {
    for {
      _    <- printLine("Hello, what is your name?")
      name <- readLine
      _    <- printLine(s"Good to meet you, ${name}!")
    } yield ()
  }

  object AsyncExample {
    import scala.concurrent.ExecutionContext.global

    def loadBodyAsync(onSuccess: String => Unit, onFailure: Throwable => Unit) : Unit =
//      global.execute(() =>
//        if (scala.util.Random.nextDouble() < 0.01) onFailure(java.io.IOException)
//        else onSuccess("Body of request")
    )

    /**
     * EXERCISE
     *
     * Using `ZIO.async`, convert the above callback based
     * API a nice clean ZIO effect
     */

    lazy val loadBodyAsyncZIO: ZIO[Any, Nothing, String] =
      ZIO.async[Any, Nothing, String] {
        callback => loadBodyAsync(body => callback(ZIO.succeed(body)), error => callback(ZIO.fail(error)))
      }

    /**
    val run =
      loadBodyAsyncZIO.flatMap(Console.printLine(_))
    */

    val run =
      for {
       body <- loadBodyAsyncZIO
       _ <- Console.printLine(body)
      } yield ()
  }
}

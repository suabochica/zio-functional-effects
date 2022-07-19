package net.degoes.zio

import zio._

/**
 * Fibers are like threads but faster and lightweight.
 *
 * Fibers operations are a similar behavior that git branching actions.
 *
 * ZIO.uninterrupt protects you that ZIO kill your effect.
 */

object ForkJoin extends ZIOAppDefault {

  val printer =
    Console.printLine(".").repeat(Schedule.recurs(10))

  /**
   *  Here we get three fibers running:
   *  1. main
   *  2. left
   *  3. right
   */
  def parallel[A, B](left: Task[A], right: Task[B]): Task[(A, B)] =
    for {
      fiberA <- left.fork
      fiberB <- right.fork
      a <- fiberA.join
      b <- fiberB.join
    } yield (a, b)

  /**
   * EXERCISE
   *
   * Using `ZIO#fork`, fork the `printer` into a separate fiber, and then
   * print out a message, "Forked", then join the fiber using `Fiber#join`,
   * and finally, print out a message "Joined".
   */
  val run =
    for {
      fiber <- printer.fork
      _     <- fiber.join
    } yield ()
}

object ForkInterrupt extends ZIOAppDefault {

  val infinitePrinter =
    Console.printLine(".").forever

  /**
   * EXERCISE
   *
   * Using `ZIO#fork`, fork the `printer` into a separate fiber, and then
   * print out a message, "Forked", then using `ZIO.sleep`, sleep for 100
   * milliseconds, then interrupt the fiber using `Fiber#interrupt`, and
   * finally, print out a message "Interrupted".
   *
   * fiber.dump: enable diagnosis on the fiber
   * fiber.join: merge a fiber to another one
   * fiber.await: picks the result but leaves both fibers separate
   * fiber.interrupt: interrupt the effect
   */
  val run =
    for {
      fiber <- infinitePrinter.fork
      _     <- ZIO.sleep(10.millis)
      exit  <- fiber.interrupt
      _     <- Console.printLine(exit)
    } yield ()

}

object ParallelFib extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Rewrite this implementation to compute nth fibonacci number in parallel.
   */
  def fib(n: Int): UIO[BigInt] = {
    def loop(n: Int, original: Int): UIO[BigInt] =
      if (n <= 1) ZIO.succeed(n)
      else
        ZIO.suspendSucceed {
          loop(n - 1, original).zipWith(loop(n - 2, original))(_ + _)
        }

    loop(n, n)
  }

  val run =
    (for {
      _ <- Console.printLine(
            "What number of the fibonacci sequence should we calculate?"
          )
      n <- Console.readLine.orDie.flatMap(input => ZIO.attempt(input.toInt)).eventually
      f <- fib(n)
      _ <- Console.printLine(s"fib(${n}) = ${f}")
    } yield ())
}

object AlarmAppImproved extends ZIOAppDefault {

  import java.io.IOException
  import java.util.concurrent.TimeUnit

  lazy val getAlarmDuration: ZIO[Any, IOException, Duration] = {
    def parseDuration(input: String): IO[NumberFormatException, Duration] =
      ZIO
        .attempt(
          Duration((input.toDouble * 1000.0).toLong, TimeUnit.MILLISECONDS)
        )
        .refineToOrDie[NumberFormatException]

    val fallback = Console.printLine("You didn't enter a number of seconds!") *> getAlarmDuration

    for {
      _ <- Console.printLine("Please enter the number of seconds to sleep: ")
      input <- Console.readLine
      duration <- parseDuration(input) orElse fallback
    } yield duration
  }

  val infinitePrinter: ZIO[Any, IOException, Nothing] =
    (Console.print(".") *> ZIO.sleep(1.second)).forever

  val printWakeUp = Console.printLine("Time to wakeup!!!")

  /**
   * EXERCISE
   *
   * Create a program that asks the user for a number of seconds to sleep,
   * sleeps the specified number of seconds using ZIO.sleep(d), concurrently
   * prints a dot every second that the alarm is sleeping for, and then
   * prints out a wakeup alarm message, like "Time to wakeup!!!".
   */

    // Race solution
  val runRace = {
    val printDots = Console.printLine(".").repeat(Schedule.spaced(1.second))
    val wakeUp = Console.printLine("Time to wakeup!!!")
    for {
      d <- getAlarmDuration
      _ <- printDots.race(ZIO.sleep(d) *> wakeUp)
    } yield ()
  }

  // Fiber solution
  val run =
    for {
      duration  <- getAlarmDuration
      printer   <- infinitePrinter.fork
      _         <- ZIO.sleep(duration) *> printer.interrupt *> printWakeUp
    } yield ()
}

object TimeoutExample extends ZIOAppDefault {
  def fib(n: Int): UIO[Int] =
    if (n <= 1) ZIO.succeed(n)
    else
      ZIO.suspendSucceed {
        fib(n - 1).zipWith(fib(n - 2))(_ + _)
      }

  /**
   * EXERCISE
   *
   * Use `ZIO#timeout` to add a timeout to the following code so that
   * it doesn't run for more than 10 milliseconds.
   *
   * Print out a message if it timed out.
   */
  lazy val run = fib(20).timeout(1.millis).flatMap{
    case None => Console.printLine("it timed out!")
    case Some(value) => Console.printLine(s"The fibonacci number is ${value}")
  }
}

object RaceExample extends ZIOAppDefault {
  def loadFromCache: Task[String] =
    ZIO.succeed("Loaded from cache!").delay(1.second)

  def loadFromDB: Task[String] =
    ZIO.succeed("Loaded from DB!").delay(500.millis)

  /**
   * EXERCISE
   *
   * Use `ZIO#race` to race the preceding two tasks and print out the
   * winning success value.
   *
   */
  lazy val run = loadFromCache.race(loadFromDB).flatMap(Console.printLine(_))
}

object RefExample1 extends ZIOAppDefault {

  def incrementer(ref: Ref[Int], n: Int) : UIO[Unit] = {
    // bad way: use ref get and set, b/c they are not atomic.
    ref.update(_ + 1).repeatN(n - 1)
  }


  val run = {
    for {
      ref   <- Ref.make(0)
      _     <- ZIO.foreach(1 to 100){ _ => incrementer(ref, 100).fork}
      value <- ref.get
      _     <- Console.printLine(s"Initial reference ${value}")
      _     <- ref.set(100)
      value <- ref.get
      _     <- Console.printLine(s"Second value of reference ${value}")
      _     <- ref.update(_ + 1)
      value <- ref.get
      _     <- Console.printLine(s"Increment value of reference ${value}")
    } yield ()
  }
}

/**
 * The Ref data type is a way for ZIO effects to utilize state. It is basically
 * a concurrent-safe version of Scala's own `var`, but integrated into ZIO.
 */
object RefExample extends ZIOAppDefault {
  import zio.Random._

  import zio.Clock._
  import zio.stm._


  /**
   * Some state to keep track of all points inside a circle,
   * and total number of points.
   */
  final case class PiState(
    inside: Ref[Long],
    total: Ref[Long]
  )

  /**
   * A function to estimate pi.
   */
  def estimatePi(inside: Long, total: Long): Double =
    (inside.toDouble / total.toDouble) * 4.0

  /**
   * A helper function that determines if a point lies in
   * a circle of 1 radius.
   */
  def insideCircle(x: Double, y: Double): Boolean =
    Math.sqrt(x * x + y * y) <= 1.0

  /**
   * An effect that computes a random (x, y) point.
   */
  val randomPoint: ZIO[Any, Nothing, (Double, Double)] =
    nextDouble zip nextDouble

  /**
   * EXERCISE
   *
   * Using `Ref#update`, make a function that adds a point into the state.
   * If the point is inside the circle then increment `PiState#inside`. In any
   * case, increment `PiState#total`.
   */
  def addPoint(point: (Double, Double), piState: PiState): UIO[Unit] = ???

  /**
   * EXERCISE
   *
   * Build a multi-fiber program that estimates the value of `pi`. Print out
   * ongoing estimates continuously until the estimation is complete.
   */
  val run =
    ???
}

object PromiseExample extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Do some computation that produces an integer. When yare done, complete
   * the promise with `Promise#succeed`.
   */
  def doCompute(result: Promise[Nothing, Int]): UIO[Unit] = ???

  /**
   * EXERCISE
   *
   * Fork the above computation in a separate fiber, giving it a promise
   * that it can use, and then wait for the promise to be completed,
   * using `Promise#await`.
   */
  lazy val waitForCompute: ZIO[Any, Nothing, Unit] = ???

  val run =
    waitForCompute
}

object FiberRefExample extends ZIOAppDefault {
  // fiberRef.make(0)
  // fiberRef.set(100)
  // fiberRef.get
  // fiberRef.update(_ + 1)

  def makeChild(ref: FiberRef[Int]) =
    for {
      _ <- ref.get.debug("child initial value")
      _ <- ref.update(_ + 1)
      _ <- ref.get.debug("child after update")
    } yield ()

  val run =
    for {
      ref   <- FiberRef.make[Int](0, identity(_), _ + _)
      _     <- ref.get.debug("parent before fork")
      child <- makeChild(ref).fork
      _     <- ref.get.debug("parent after fork")
      _     <- child.join
      _     <- ref.get.debug("parent after join")
    } yield ()
}

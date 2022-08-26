/**
 * ZIO provides features specifically designed to improve your experience
 * deploying, scaling, monitoring, and troubleshooting ZIO applications.
 * These features include async stack traces, fiber dumps, logging hooks,
 * and integrated metrics and monitoring.
 *
 * In this section, you get to explore the operational side of ZIO.
 */
package net.zio.ops

import zio._

import zio.test._
import zio.test.TestAspect._

object AsyncTraces extends ZIOSpecDefault {
  def spec =
    suite("AsyncTraces") {

      /**
       * EXERCISE
       *
       * Pull out the `traces` associated with the following sandboxed
       * failure, and verify there is at least one trace element.
       */
      test("traces") {
        def async =
          for {
            _ <- ZIO.sleep(1.millis)
            _ <- ZIO.fail("Uh oh!")
          } yield ()

        def traces(cause: Cause[String]): List[StackTrace] = {
          println(cause.prettyPrint)
          cause.traces
        }

        Live.live(for {
          cause <- async.sandbox.flip
          ts    = traces(cause)
          // _ <- ZIO.foreach(ts)(trace => Console.printLine(trace.prettyPrint())
        } yield assertTrue(ts(0).stackTrace.length > 0))
      }
    }
}

object FiberDumps extends ZIOSpecDefault {
  def spec =
    suite("FiberDumps") {

      /**
       * EXERCISE
       *
       * Compute and print out all fiber dumps of the fibers running in this test.
       */
      test("dump") {
        // A command for check concurrent code
        val example =
          for {
            promise <- Promise.make[Nothing, Unit]
            blocked <- promise.await.forkDaemon
            child1  <- ZIO.foreach(1 to 100000)(_ => ZIO.unit).forkDaemon
          } yield ()

        for {
          supervisor <- Supervisor.track(false)
          _          <- example.supervised(supervisor)
          children   <- supervisor.value
          // TODO: Compiler error: type mismatch
          //_          <- ZIO.foreach(children)(child => child.dump.flatMap(dump => dump.prettyPrint.flatMap(Console.printLine())))
        } yield assertTrue(children.length == 2)
      } @@ flaky
    }
}

object Logging extends ZIOAppDefault {
  import ZIOAspect.annotated
  def run = {
    // ZIO.log("Hello") @@ annotated("userID", "Sherlock") @@ LogLevel.Error
    ZIO.logLevel(LogLevel.Error) {
      ZIO.logAnnotate("🕵🏾‍", "Sherlock"){
        ZIO.log("👋🏾")
      }
    }
  }

  // When log errors, use 'logErrorCause' to include the stack trace in the log
}

object Metrics extends ZIOAppDefault {
  // Metric Types: Counter, Gauge, Histogram(Duration), Summary(Percentages), Frequency

  import zio.metrics._

  val totalRequest =
    Metric.counter("total request").tagged("route", "billing").tagged("api_version", "v1")
  val concurrentRequest =
    Metric.gauge("concurrent_request").tagged("route", "billing").tagged("api_version", "v1")
  def handleRoute(): Task[Unit] = Console.printLine("Handling route")
  def run = {
    /*
    for {
      _ <- totalRequest.update(1)
      v <- totalRequest.value
      _ <- Console.printLine(s"Total request: $v")
    } yield ()
     */

    /*
    for {
      _ <- ZIO.acquireReleaseWith(concurrentRequest.update(+1))(_ => concurrentRequests.update(-1))(_ => concurrentRequest.update(1))
      v <- totalRequest.value
      _ <- Console.printLine(s"Total request: $v")
    } yield ()
     */

    for {
      _ <- handleRoute @@ totalRequest.trackSuccessWith((_: Unit) => 1L)
      v <- totalRequest.value
      _ <- Console.printLine(s"Total request: $v")
    } yield ()
  }

}

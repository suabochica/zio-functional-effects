/**
 * TESTING
 *
 * To facilitate robust testing of ZIO applications, ZIO includes a testkit
 * called _ZIO Test_. ZIO Test is a lightweight but rich testing environment
 * that seamlessly integrates with ZIO, providing access to all of ZIO's
 * power from within unit tests and full testability for all ZIO services.
 *
 * In this module, you will explore how you can use _ZIO Test_ to write
 * powerful unit, integration, and system tests that ensure your ZIO
 * applications perform correctly in production.
 */
package net.zio

import zio._
import zio.test.TestAspect._
import zio.test._

/**
 * SPECS
 *
 * ZIO Test specs are fully-functional, executable programs. They can be run
 * without an IDE, because they all have a main function. If you are using the
 * SBT test runner or the IntelliJ IDEA ZIO plug-in, you can also run your tests
 * directly from SBT or your IDE.
 */
object SimplestSpec extends ZIOSpecDefault {

  /**
   * EXERCISE
   *
   * Using sbt or your IDE, run `SimplestSpec` by using its `main` function (not the test runner).
   */
  def spec = suite("SimplestSpec")(
    test("simple test") {
      for {
        result <- ZIO.succeed(2 + 2)
      } yield assertTrue(result == 4)
      // 'assertTrue' is a function that given a Boolean value it returns a test result
      // 'assertTrue' is a macro
    },
    test("foo") {
      assertTrue(true)
    }
  )
}

object SimplestSpecFunctionStyle extends ZIOSpecDefault {
  def simpleTest =
    test("simple test") {
      for {
        result <- ZIO.succeed(2 + 2)
      } yield assertTrue(result == 4)
    }

  def dummyTest =
    test("foo") {
      assertTrue(true)
    }

  def spec = suite("SimplestSpec")(
    simpleTest,
    dummyTest
  )
}

/**
 * ASSERTIONS
 *
 * ZIO Test operates using assertions, which use macros to provide very
 * powerful error messages and reporting. Assertions produce values,
 * which compose using a variety of operators.
 */
object BasicAssertions extends ZIOSpecDefault {
  def spec = suite("BasicAssertions") {
    trait Building {
      def contents: String
    }
    object House extends Building {
      def contents = "bed, coffee pot, kitchen"
    }
    object Barn extends Building {
      def contents = "hay, goats, feed"
    }
    object Shed extends Building {
      def contents = "needle, broom"
    }

    val buildings = List(House, Barn, Shed)

    test("2 + 2 == 4") {

      /**
       * EXERCISE
       *
       * Using `assertTrue`, assert that 2 + 2 == 4.
       */
      assertTrue(2 + 2 == 4)
    } +
      test("sherlock misspelling") {

        /**
         * EXERCISE
         *
         * Examine the output of this failed test. Then fix the test so that it passes.
         */
        assertTrue("sherlock".contains("sher"))
      } +
      test("multiple assertions") {
        val string = "cannac"

        /**
         * EXERCISE
         *
         * Using the `&&` operator of `Assert`, verify the following properties
         * about `string`:
         *
         *  - The string is 6 letters in length
         *  - the string starts with "can"
         *  - the reverse of the string is equal to itself
         */

        assertTrue(string.length == 6) &&
          assertTrue(string.startsWith("can")) &&
          assertTrue(string.reverse == string)

        // It works also!
        //        assertTrue(
        //          string.length == 6 &&
        //          string.startsWith("can") &&
        //          string.reverse == string
        //        )
      } +
      test("needle test") {
        /**
         * EXERCISE
         *
         * Using `+`, add another test to the suite, which you can create with
         * `test`, as above. This test should verify that the contents of one
         * of the buildings in `buildings` contains a `needle`.
         */

        // ZIO 1
        // Assertion.exists(Assertion.containString("needle"))
        assertTrue(buildings.exists(b => b.contents.contains("needle")))
      }
  }
}

/**
 * ZIO ASSERTIONS
 *
 * Most assertions in ZIO Test will be effectful, rather than pure. Using the
 * same syntax, ZIO lets you write effectful tests.
 */
object BasicAssertionsZIO extends ZIOSpecDefault {
  /**
   * suite("dynamic tests") {
   * ZIO.succeed {
   * val entries: List[Entry] = loadEntries()
   *
   * entries.foldLeft(...) {
   * ...
   * } : Spec
   * }
   * }
   */

  def spec = suite("BasicAssertionsZIO") {
    test("incrementing a ref") {

      /**
       * EXERCISE
       *
       * Using `assertTrue`, assert that incrementing a zero-valued ref by one
       * results in 1.
       */
      for {
        ref <- Ref.make(0)
        v <- ref.updateAndGet(_ + 1)
      } yield assertTrue(false)
    } +
      test("multiple assertions") {

        /**
         * EXERCISE
         *
         * Using the `&&` operator of `Assert`, verify the following properties
         * about `v`:
         *
         *  - It is an even number
         *  - It is greater than 0
         */
        for {
          ref <- Ref.make(0)
          rand <- Random.nextIntBetween(1, 4)
          v <- ref.updateAndGet(_ + 1).repeatN(rand * 2)
        } yield assertTrue(v % 2 == 0) && assertTrue(v > 0)
      }
  }
}

/**
 * TEST ASPECTS
 *
 * ZIO Test offers _test aspects_, which are values that allow modifying specs,
 * whether suites or individual tests. Test aspects are kind of like annotations,
 * except they are type-safe, non-magical, and first class values that can be
 * transformed and composed with other test aspects.
 *
 * Test aspects can add features like retrying tests, ignoring tests, running
 * tests only on a certain platform, and so forth.
 */
object BasicTestAspects extends ZIOSpecDefault {

  def spec = suite("BasicTestAspects") {
    test("ignore") {

      /**
       * EXERCISE
       *
       * Using `TestAspect.ignore`, add the `ignore` aspect to this test so that
       * the failure is ignored.
       */
      assertTrue(false)
    } @@ ignore +
      test("flaky") {

        /**
         * EXERCISE
         *
         * Using `TestAspect.flaky`, mark this test as flaky so that it will pass so
         * long as it sometimes succeeds.
         */
        for {
          number <- Random.nextInt
        } yield assertTrue(number % 2 == 0)
      } @@ flaky +
      test("nonFlaky") {

        /**
         * EXERCISE
         *
         * Using `TestAspect.nonFlaky`, mark this test as non-flaky so that ZERO
         * failures are permitted.
         */
        for {
          number <- Random.nextIntBetween(0, 100)
        } yield assertTrue(number * 2 % 2 == 0)
      } @@ nonFlaky +
      suite("sequential") {

        /**
         * EXERCISE
         *
         * Add the `sequential` aspect to this suite and observe the change in
         * output to the console.
         */
        test("Test 1") {
          for {
            _ <- Live.live(ZIO.sleep(10.millis))
            _ <- Console.printLine("Test 1")
          } yield assertTrue(true)
        } +
          test("Test 2") {
            for {
              _ <- Console.printLine("Test 2")
            } yield assertTrue(true)
          }
      } @@ sequential
  }
}

/**
 * TEST FIXTURES
 *
 * ZIO can execute arbitrary logic before, after, or before and after
 * tests individually, or all tests in a suite. This ability is sometimes
 * used for "test fixtures", which allow developers to perform custom
 * setup / tear down operations required for running tests.
 *
 * In case that after, before and around won't enough, we can use layers.
 */
object TestFixtures extends ZIOSpecDefault {
  val beforeRef = new java.util.concurrent.atomic.AtomicInteger(0)
  val aroundRef = new java.util.concurrent.atomic.AtomicInteger(0)

  val incAroundRef = ZIO.succeed(aroundRef.incrementAndGet())
  val decAroundRef = ZIO.succeed(aroundRef.decrementAndGet())

  val incBeforeRef: UIO[Any] = ZIO.succeed(beforeRef.incrementAndGet())

  def spec = suite("TestFixtures") {

    /**
     * EXERCISE
     *
     * Using `TestAspect.before`, ensure the `incBeforeRef` effect is executed
     * prior to the start of the test.
     */

    test("before") {
      for {
        value <- ZIO.succeed(beforeRef.get)
      } yield assertTrue(value > 0)
    } @@ before(incBeforeRef) +
      test("after") {

        /**
         * EXERCISE
         *
         * Using `TestAspect.after`, ensure the message `done with after` is printed
         * to the console using `ZIO.debug`.
         */

        for {
          _ <- Console.printLine("after")
        } yield assertTrue(true)
      } @@ after(Console.printLine("Done with after")) +
      test("around") {

        /**
         * EXERCISE
         *
         * Using `TestAspect.around`, ensure the `aroundRef` is incremented before and
         * decremented after the test.
         */

        for {
          value <- ZIO.succeed(aroundRef.get)
        } yield assertTrue(value == 1)
      } @@ around(incAroundRef, decAroundRef)
  }
}

/**
 * TEST SERVICES
 *
 * By default, ZIO tests use test versions of all the standard services
 * baked into ZIO, including Random, Clock, System, and Console.
 * These allow you to programmatically control the services, such as
 * adjusting time, setting up fake environment variables, or inspecting
 * console output or providing console input.
 */
object TestServices extends ZIOSpecDefault {
  def spec =
    suite("TestServices") {

      /**
       * EXERCISE
       *
       * Using `TestClock.adjust`, ensure this test passes without timing out.
       */

      test("TestClock") {
        for {
          fiber <- Clock.sleep(1.second).as(42).fork
          _ <- TestClock.adjust(1.second)
          value <- fiber.join
        } yield assertTrue(value == 42)
      } +
        test("TestSystem") {

          /**
           * EXERCISE
           *
           * Using `TestSystem.putEnv`, set an environment variable to make the
           * test pass.
           */

          for {
            name <- System.env("name").some
          } yield assertTrue(name == "Sherlock Holmes")
        } @@ ignore +
        test("TestConsole") {

          /**
           * EXERCISE
           *
           * Using `TestConsole.feedLines`, feed a name into the console such that
           * the following test passes.
           */

          for {
            _ <- Console.printLine("What is your name?")
            name <- Console.readLine
          } yield assertTrue(name == "Sherlock Holmes")
        } @@ ignore +
        test("TestRandom") {

          /**
           * EXERCISE
           *
           * Using `TestRandom.feedInts`, feed the integer 5 into the Random
           * generator so the test will pass.
           */

          for {
            _ <- TestRandom.feedInts(5)
            _ <- TestConsole.feedLines("5")
            number <- Random.nextInt
            _ <- Console.printLine("Guess a random number between 0 - 10: ")
            guess <- Console.readLine
            result <- if (guess == number.toString) Console.printLine("Good job!").as(true)
            else Console.printLine("Try again!").as(false)
          } yield assertTrue(result)
        } +
        test("Live") {

          /**
           * EXERCISE
           *
           * Some times it is necessary to run code against a live standard
           * service, rather than one of the test services baked into ZIO Test.
           * A useful function for doing this is `Live.live`, which will ensure
           * the provided effect runs using the live services.
           */

          for {
            now <- Clock.instant.map(_.getEpochSecond())
          } yield assertTrue(now > 0)
        } @@ withLiveClock
    }
}

/**
 * INTEGRATION/SYSTEM ASPECTS
 *
 * Some ZIO Test aspects are designed for more advanced integration and system
 * tests.
 */
object IntegrationSystem extends ZIOSpecDefault {

  /**
   * EXERCISE
   *
   * Explore jvmOnly, windows, linux, ifEnv, and other test aspects that
   * are useful for running platform-specific or integration / system tests.
   */
  def spec = suite("IntegrationSystem")() @@
    TestAspect.ifEnv("CI_MODE")(mode => mode == "STAGING" || mode == "NIGHTLY") @@
    // sbt ""test -- -tags billing
    TestAspect.tag("billing")
}

/**
 * CUSTOM LAYERS
 *
 * The code you are testing may use its own layers, to provide access to
 * other services required by your application. This is especially true
 * for business logic, which may be assembled from high-level layers
 * that allow expressing business logic in a direct style.
 *
 * ZIO Test allows you to provide custom layers in a variety of ways
 * to your tests.
 */
object CustomLayers extends ZIOSpecDefault {
  final case class User(id: String, name: String, age: Int)

  trait UserRepo {
    def getUserById(id: String): Task[Option[User]]

    def updateUser(user: User): Task[Unit]
  }

  object UserRepo {
    def getUserById(id: String): RIO[UserRepo, Option[User]] =
      ZIO.serviceWithZIO[UserRepo](_.getUserById(id))

    def updateUser(user: User): RIO[UserRepo, Unit] =
      ZIO.serviceWithZIO[UserRepo](_.updateUser(user))
  }

  final case class TestUserRepo(ref: Ref[Map[String, User]]) extends UserRepo {

    /**
     * EXERCISE
     *
     * Implement the following method of the user repo to operate on the
     * in-memory test data stored in the Ref.
     */
    def getUserById(id: String): Task[Option[User]] =
      ref.get.map(_.get(id))

    /**
     * EXERCISE
     *
     * Implement the following method of the user repo to operate on the
     * in-memory test data stored in the Ref.
     */
    def updateUser(user: User): Task[Unit] =
      ref.update(_.updated(user.id, user))
  }

  /**
   * EXERCISE
   *
   * Create a test user repo layer and populate it with some test data.
   */
  lazy val testUserRepo: ULayer[UserRepo] = {
    // A layer is a recipe to build an implementation
    ZLayer {
      for {
        ref <- Ref.make[Map[String, User]](Map(
          "john@doe.com" -> User("john@doe.com", "John Doe", 30),
          "jane@street.com" -> User("jane@street.com", "Jane Street", 20),
          "sherlock@holmes.com" -> User("sherlock@holmes.com", "Sherlock Holmes", 42)
        ))
      } yield TestUserRepo(ref)
    }
  }

  def spec =
    suite("CustomLayers") {
      test("provideCustomLayer") {

        /**
         * EXERCISE
         *
         * In order to complete this exercise, you will have to make several
         * changes. First, use `UserRepo.getUserById` to retrieve the user
         * associated with the id. Then check the age is 42. To make the
         * test compile, you will have to `provideCustomLayer` on the test.
         * Finally, to make the test pass, you will have to create test
         * data matches your test expectations.
         */

        for {
          user <- UserRepo.getUserById("sherlock@holmes.com").some
        } yield assertTrue(user.age == 42)
        assertTrue(false)
      } @@ ignore +
        suite("shared layer") {

          /**
           * EXERCISE
           *
           * Layers can be shared across all the tests in a suite.
           *
           * Use `provideCustomLayerShared` to provide a layer that is shared
           * across both of the following (sequentially executed) tests. Then
           * add a user in the first test that is then retrieved in the second.
           */
          test("adding a user") {
            assertTrue(false)
          } +
            test("getting a user") {
              assertTrue(false)
            }
        } @@ sequential @@ ignore
    }.provideCustomLayer(testUserRepo) // Provide whatever layer you want
}

/*
trait UserRepo
object Example extends ZIOSpec[UserRepo] {
  val bootstrap: zio.ZLayer[zio.Scope, Any, UserRepo] = ??? // base on reference identity
  def spec: zio.test.Spec[UserRepo with TestEnvironment with Scope] = ???
}
 */

/**
 * GRADUATION PROJECT
 *
 * To graduate from this section, you will choose and complete one of the
 * following projects under the assistance of the instructor:
 *
 * 1. Implement a custom `TestAspect` that provides features or functionality
 * you might like to use in your own unit tests.
 *
 * 2. Design an `EmailService` for sending emails. Then create a test
 * implementation that allows simulating failures and successes, and
 * which captures sent emails for purposes of testing. Finally,
 * create a layer for the test email service and use it in a test.
 *
 */
object Graduation extends ZIOSpecDefault {
  // mySpec @@ dataProvider(List(1, 2, 3))
  trait DataProvider[+A] {
    def get: UIO[A]
  }

  object DataProvider {
    def get[A: Tag]: ZIO[DataProvider[A], Nothing, A] = ZIO.serviceWithZIO[DataProvider[A]](_.get)

    /*
    def apply[A](as: A*): ZLayer[Any, Nothing, DataProvider[A]] =
      ZLayer {
        ref <- Ref.make()
      }
     */
    def none[A: Tag]: ZLayer[Any, Nothing, DataProvider[A]] = ZLayer.succeed(new DataProvider[A] {
      def get: UIO[A] = ZIO.dieMessage("No data provider")
    })

  }

  /* TODO: Fix compiler error
  def dataProvider[A: Tag](as: A*) =
    new TestAspectAtLeastR[DataProvider[A]] {
      def some[R <: DataProvider[A], E](spec: Spec[R, E])(implicit trace: Trace): Spec[R, E] =
        as.map { (a: A) =>
          spec.provideSomeLayer {
            ZLayer.succeed[DataProvider[A]] {
              new DataProvider[A] {
                def get: UIO[A] = ZIO.succeed(a)
              }
            }
          }
        }.reduceOption(_ + _).getOrElse(Spec.empty)
    }
   */
  def dataProvider[A: Tag](as: A*) =
    new TestAspectAtLeastR[DataProvider[A]] {
      def some[R <: DataProvider[A], E](spec: Spec[R, E])(implicit trace: Trace): Spec[R, E] =
        as.map { (a: A) =>
          spec.provideSomeLayer[R] {
            ZLayer.succeed[DataProvider[A]] {
              new DataProvider[A] {
                def get: UIO[A] = ZIO.succeed(a)
              }
            }
          }
        }.reduceOption(_ + _).getOrElse(Spec.empty)
    }

  def short =
    new TestAspectAtLeastR[Live] {
      def some[R <: Live, E](spec: Spec[R, E])(implicit trace: Trace): Spec[R, E] =
        spec @@ TestAspect.timeout(500.millis)
    }

  def spec = suite("Graduation")(
      test("custom test aspect") {
        for {
          data <- DataProvider.get[Int]
        } yield assertTrue(data > 0)
      } @@ dataProvider(1)
    ).provideCustomLayer(DataProvider.none[Int])
}

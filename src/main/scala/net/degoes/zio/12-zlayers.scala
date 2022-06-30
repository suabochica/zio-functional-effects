package net.degoes.zio

import zio._
import java.text.NumberFormat
import java.nio.charset.StandardCharsets

/**
 * Structuring Services in Scala with ZIO and Zlayer
 *
 * - video: https://www.youtube.com/watch?v=PaogLRrYo64
 * - blog: https://blog.rockthejvm.com/structuring-services-with-zio-zlayer/
 */

object Greeting extends ZIOAppDefault {
  // ZIO[-R, +E, +A] = "Effects"
  // - R: The input of the computation
  // - R => Either[E, A]
  // - E: The error of the computation
  // - A: The success of the computation
  // The effect is no the effect, the effect is your data about the effect.

  val meaningOfLife = ZIO.succeed(42)
  val aFailure = ZIO.fail("Something went wrong")

  val greeting = for {
    _     <- Console.printLine("Hi, what is your name?")
    name  <- Console.readLine
    _     <- Console.printLine(s"Hello, ${name}, welcome to JVM")
  } yield ()

  val run = greeting
}

object UserEmailExample extends ZIOAppDefault {
  /*
    Creating heavy apps involve services:
      - interacting with storage layer
      - business logic
      - front-facing APIs
      - communicating with other services
   */

  // Service structure
  // 1. Service definition
  // 2. Service implementation
  // 3. Front-facing API

  case class User(name: String, email: String)
  object UserEmailer {
    type UserEmailerEnv = User with Service
    // Service definition
    trait Service {
      def notify(user: User, message: String): Task[Unit]
    }

    // Service implementation
    val live:ZLayer[Any, Nothing, UserEmailer.Service] = ZLayer.succeed(new Service {
      override def notify(user: User, message: String) =  {
        println(s"[User emailer] Sending '${message} to ${user.email}")
      }
    })

    // Front-facing API
    def notify(user: User, message:String): ZIO[UserEmailer.Service, Throwable, Unit] =
    // ZIO.accessM(hasService => hasService.get.notify(user, message))

    val jim = User("Jim", "jim@theoffice.com")
    val message = "That's no true"

  }

  object UserDatabase {
    type UserDatabaseEnv = User with Service

    trait Service {
      def insert(user: User): Task[Unit]
    }

    val live = ZLayer.succeed(new Service {
      override def insert(user: User): Task[Unit] = {
        Console.printLine(s"[Database] insert into public.user values ('${user.email}')")
      }
    })

    def insert(user: User): ZIO[UserDatabaseEnv, Throwable, Unit] = ZIO.accessM(_.get.insert(user))
  }

  val run =
    UserEmailer.notify(jim, message)  // The kind effect
      .provideLayer(UserEmailer.live) // Provide the input for that effect ... DI
      .exitCode
}

package net.degoes.zio

import net.degoes.zio.UserEmailExample.UserEmailer.UserDatabase.UserDatabaseEnv
import net.degoes.zio.UserEmailExample.UserEmailer.UserSubscription
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

  // Service Structure Pattern
  // -------------------------
  // 1. Service definition
  // 2. Service implementation
  // 3. Front-facing API (TODO: Check the replace of accessM in last ZIO version)

  case class User(name: String, email: String)

  object UserEmailer {
    type UserEmailerEnv = User with Service
    // type UserEmailerEnv = Has[User, Service] TODO: Check the Has[] replace
    // 1. Service definition
    trait Service {
      def notify(user: User, message: String): Task[Unit]
    }

    // 2. Service implementation
    val live:ZLayer[Any, Nothing, UserEmailer.Service] = ZLayer.succeed(new Service {
      override def notify(user: User, message: String) =  {
        println(s"[User emailer] Sending '${message} to ${user.email}")
      }
    })

    // 3. Front-facing API
    def notify(user: User, message:String): ZIO[UserEmailer.Service, Throwable, Unit] =
      ZIO.accessM(hasService => hasService.get.notify(user, message))


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

      def insert(user: User): ZIO[UserDatabaseEnv, Throwable, Unit] =
        ZIO.accessM(_.get.insert(user)) Check the replace of accessM
    }

    // Horizontal Composition
    // ----------------------

    // ZLayer[In1, Err1, Out1] ++ ZLayer[In2, Err2, Out2] =>
    //  ZLayer[In1 with In2, super(Err1, Err2), Out1 with Out2]

    // Let's combine UserEmailer with UserDatabase

    import UserDatabase._
    import UserEmailer._

    val userBackendLayer: ZLayer[Any, Nothing, UserDatabaseEnv with UserEmailerEnv] =
      UserDatabase.live ++ UserEmailer.live


    // Vertical Composition
    // --------------------

    //

    object UserSubscription {
      type UserSubscriptionEnv = Has[UserSubscription.Service]
      // 1. Service definition
      class Service(notifier: UserEmailer.Service, userDatabase: UserDatabase.Service) {
        def subscribe(user: User): Task[User] =
          for {
            _ <- userDatabase.insert(user)
            _ <- notifier.notify(user, s"Welcome ${user.name}! We have some nice ZIO content for you")
          } yield user
      }


      // 2. Service implementation
      val live = ZLayer.fromService[UserEmailer.Service, UserDatabase.Service] {
        (userEmailer, userDatabase) => new Service(userEmailer, userDatabase)
      }

      // 3. Front-facing API
      def subscribe(user: User): ZIO[UserSubscriptionEnv, Throwable, User] =
        ZIO.accessM(_.get.subscribe(user))

    }

    val jim = User("Jim", "jim@theoffice.com")
    val message = "That's no true"

    val notifyJim =
      UserEmailer.notify(jim, message)  // The kind effect
        .provideLayer(UserEmailer.live) // Provide the input for that effect ... DI
        .exitCode

    import UserSubscription._
    val userSubscriptionLayer: ZLayer[Any, Nothing, UserSubscriptionEnv] = userBackendLayer >>> UserSubscription.live

    val run =
      UserSubscription.subscribe(jim)
        .provideLayer(userSubscriptionLayer)
        .exitCode
  }
}

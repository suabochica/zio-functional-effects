package net.degoes

import java.io.BufferedInputStream
import scala.annotation.tailrec
import scala.util.control.NonFatal

/*
 * INTRODUCTION
 *
 * In Functional Design, a functional domain consists of three things:
 *
 * 1. MODELS. A functional model, which is an immutable data type that models a
 *    solution to problems in the domain of interest.
 *
 * 2. CONSTRUCTORS. Constructors that allow constructing solutions to simple problems.
 *
 * 3. OPERATORS. Operators that solving more complex problems by transforming
 *    and combining solutions for sub problems.
 *
 * Functional domains allow modeling solutions to problems in a specific domain.
 * Done properly, a small set of primitives can be so powerful, they can be used
 * compositionally to describe all possible solutions in that domain.
 *
 * A functional domain can be regarded as a type of internal domain-specific
 * language (DSL), which is designed specifically for expressing compositional
 * solutions to some category of domain problems.
 *
 * ZIO is an example of a domain for input/output, whose effect type lets you
 * solve async/concurrent/resourceful problems, and whose operators let you
 * assemble large solutions from small solutions.
 *
 * In this section, you'll explore the role of operators in Functional Design.
 *
 * OPERATORS
 *
 * Operators come in two primary flavors: unary operators, which are the ones
 * that transform solutions into other solutions values with desired
 * properties; and binary operators, which combine two solutions for
 * subproblems into a solution for a larger problem.
 *
 * Composable operators accept and return similar types, which allows them
 * to be used repeatedly. For example, the `+` binary operator for integers
 * allows repeatedly adding numbers together (`1 + 2 + 3 + 4`) because the
 * return value of the operator is compatible with its input type.
 *
 * Composable operators allow you to generate a large variety of solutions out
 * of a smaller number of primitives, simply transforming and composing other
 * solutions.
 */

/**
 * FILE I/O - EXERCISE SET 1
 *
 * Consider an ETL application that loads a lot of data from files and FTP
 * servers using Java's InputStream.
 */
object input_stream {
  import java.io.InputStream

  // Domain: All possible ways to construct an InputStream
  // Model: A case class containing a factory to create an input stream
  // Constructors: empty, fromFile, fromFTP
  // Operators: ???
  final case class IStream(createInputStream: () => InputStream) { self =>

    /**
     * EXERCISE 1
     *
     * Create an operator `++` that returns a new `IStream`, which will read
     * all data from the first input stream, and then when that one is
     * exhausted, it will close the first input stream, make the second
     * input stream, and continue reading from the second one.
     */
    def ++(that: => IStream): IStream =
      IStream {() =>
        new InputStream {
          var current = self.createInputStream()
          val isFirst = true

          @tailrec
          override def read(): Int = {
            val byte = current.read()

            if (byte < 0 && isFirst) {
              current.close()
              current = that.createInputStream()
              read()
            } else byte
          }

          override def close(): Unit = current.close()
        }
      }

    /**
     * EXERCISE 2
     *
     * Create an operator `orElse` that returns a new `IStream`, which will
     * try to create the first input stream, but if that fails by throwing
     * an exception, it will then try to create the second input stream.
     */
    def orElse(that: => IStream): IStream = IStream { () =>
      try {
        self.createInputStream()
      } catch {
        case NonFatal(_) => that.createInputStream()
      }
    }

    /**
     * EXERCISE 3
     *
     * Create an operator `buffered` that returns a new `IStream`, which will
     * create the input stream, but wrap it in Java's `BufferedInputStream`
     * before returning it.
     */
    def buffered: IStream =
      IStream {() => new BufferedInputStream(self.createInputStream())}
  }
  object IStream {

    /**
     * Creates an empty stream.
     */
    val empty: IStream = IStream(() => new java.io.ByteArrayInputStream(Array.ofDim[Byte](0)))

    /**
     * Defers the construction of a `IStream` that might fail.
     */
    def suspend(is: => IStream): IStream =
      IStream(() => is.createInputStream())
  }

  /**
   * EXERCISE 4
   *
   * Construct an IStream that will read the data from `primary`,
   * but if that fails, it will assemble the data from all the
   * `fragments` by concatenating them into one. Regardless of
   * where the data comes from, everything should be buffered.
   */
  lazy val allData: IStream = primary.orElse(fragments.foldLeft(IStream.empty)(_ ++ _)).buffered

  lazy val primary: IStream         = ???
  lazy val fragments: List[IStream] = ???
}

/**
 * EMAIL CLIENT - EXERCISE SET 2
 *
 * Consider a web email interface, which allows users to filter emails and
 * direct them to specific folders based on custom criteria.
 */
object email_filter {
  final case class Address(emailAddress: String)
  final case class Email(sender: Address, to: List[Address], subject: String, body: String)

  final case class EmailFilter(matches: Email => Boolean) { self =>

    /**
     * EXERCISE 1
     *
     * Add an "and" operator that will match an email if both the first and
     * the second email filter match the email.
     */
    def &&(that: EmailFilter): EmailFilter = EmailFilter(email => self.matches(email) && that.matches(email))

    /**
     * EXERCISE 2
     *
     * Add an "or" operator that will match an email if either the first or
     * the second email filter match the email.
     */
    def ||(that: EmailFilter): EmailFilter = EmailFilter(email => self.matches(email) || that.matches(email))

    /**
     * EXERCISE 3
     *
     * Add a "negate" operator that will match an email if this email filter
     * does NOT match an email.
     */
    def unary_! : EmailFilter = EmailFilter(email => !self.matches(email))
  }
  object EmailFilter {
    def senderIs(address: Address): EmailFilter = EmailFilter(_.sender == address)

    def recipientIs(address: Address): EmailFilter = EmailFilter(_.to.contains(address))

    def subjectContains(phrase: String): EmailFilter = EmailFilter(_.subject.contains(phrase))

    def bodyContains(phrase: String): EmailFilter = EmailFilter(_.body.contains(phrase))
  }

  /**
   * EXERCISE 4
   *
   * Make an email filter that looks for subjects that contain the word
   * "discount", bodies that contain the word "N95", and which are NOT
   * addressed to "john@doe.com". Build this filter up compositionally
   * by using the defined constructors and operators.
   */
  lazy val emailFilter1 =
    subjectThatContainsDiscount &&
    bodyThatContainsN95 &&
    !toJohnDoe

  val subjectThatContainsDiscount = EmailFilter.subjectContains("discount")
  val bodyThatContainsN95 = EmailFilter.bodyContains("N95")
  val toJohnDoe = EmailFilter.recipientIs(Address("john@doe.com"))
}

/**
 * DATA TRANSFORM - EXERCISE SET 3
 *
 * Consider an email marketing platform, which allows users to upload contacts.
 */
object contact_processing {
  final case class SchemaCSV(columnNames: List[String]) {
    def relocate(i: Int, j: Int): Option[SchemaCSV] =
      if (i < columnNames.length && j < columnNames.length)
        Some(copy(columnNames = columnNames.updated(i, columnNames(j)).updated(j, columnNames(i))))
      else None

    def delete(i: Int): SchemaCSV = copy(columnNames = columnNames.take(i) ++ columnNames.drop(i + 1))

    def add(name: String): SchemaCSV = copy(columnNames = columnNames ++ List(name))
  }

  final case class ContactsCSV(schema: SchemaCSV, content: Vector[Vector[String]]) { self =>
    def get(column: String): Option[Vector[String]] =
      columnOf(column).map(i => content.map(row => row(i)))

    def add(columnName: String, column: Vector[String]): ContactsCSV =
      copy(schema = schema.add(columnName), content = content.zip(column).map { case (xs, x) => xs :+ x })

    def columnNames: List[String] = schema.columnNames

    def columnOf(name: String): Option[Int] = {
      val index = columnNames.indexOf(name)

      if (index >= 0) Some(index) else None
    }

    def get(row: Int, columnName: String): Option[String] =
      for {
        col   <- columnOf(columnName)
        row   <- content.lift(row)
        value <- row.lift(col)
      } yield value

    def rename(oldColumn: String, newColumn: String): ContactsCSV = {
      val index = schema.columnNames.indexOf(oldColumn)

      if (index < 0) self
      else copy(schema = SchemaCSV(schema.columnNames.updated(index, newColumn)))
    }

    def relocate(column: String, j: Int): Option[ContactsCSV] =
      columnOf(column).flatMap { i =>
        if (i < columnNames.length && j < columnNames.length)
          schema
            .relocate(i, j)
            .map(schema =>
              copy(schema = schema, content = content.map(row => row.updated(j, row(i)).updated(i, row(j))))
            )
        else None
      }

    def delete(column: String): ContactsCSV =
      columnOf(column).map { i =>
        copy(schema = schema.delete(i), content = content.map(row => row.take(i) ++ row.drop(i + 1)))
      }.getOrElse(self)

    def combine(column1: String, column2: String)(
      newColumn: String
    )(f: (String, String) => String): Option[ContactsCSV] =
      for {
        index1 <- columnOf(column1)
        index2 <- columnOf(column2)
        column = content.map(row => f(row(index1), row(index2)))
      } yield add(newColumn, column).delete(column1).delete(column2)
  }

  /**
   * A `MappingResult[A]` is the result of mapping a schema. It is either a failure, in which case
   * there are 0 or more errors; or it is a success, in which case there are 0 or more warnings.
   */
  sealed trait MappingResult[+A] { self =>
    import MappingResult._

    def flatMap[B](f: A => MappingResult[B]): MappingResult[B] =
      self match {
        case Success(value, warnings) =>
          f(value) match {
            case Success(value, warnings2) => Success(value, warnings ++ warnings2)
            case Failure(errors)           => Failure(errors)
          }

        case Failure(errors) => Failure(errors)
      }

    def orElse[A1 >: A](that: MappingResult[A1]): MappingResult[A1] =
      self match {
        case Success(value, warnings) => Success(value, warnings)
        case Failure(errors) =>
          that match {
            case Success(value, warnings) => Success(value, warnings)
            case Failure(errors2)         => Failure(errors ++ errors2)
          }
      }

    def map[B](f: A => B): MappingResult[B] =
      self match {
        case Success(value, warnings) => Success(f(value), warnings)
        case Failure(errors)          => Failure(errors)
      }

    def zip[B](that: MappingResult[B]): MappingResult[(A, B)] =
      (self, that) match {
        case (Success(value1, warnings1), Success(value2, warnings2)) =>
          Success((value1, value2), warnings1 ++ warnings2)
        case (Failure(errors), _) => Failure(errors)
        case (_, Failure(errors)) => Failure(errors)
      }

    def zipWith[B, C](that: MappingResult[B])(f: (A, B) => C): MappingResult[C] = (self zip that).map(f.tupled)
  }
  object MappingResult {
    final case class Success[+A](value: A, warnings: List[String] = Nil) extends MappingResult[A]
    final case class Failure(errors: List[String])                       extends MappingResult[Nothing]

    def fromOption[A](option: Option[A], error: String): MappingResult[A] =
      option match {
        case None    => Failure(error :: Nil)
        case Some(v) => Success(v)
      }
  }

  final case class SchemaMapping(map: ContactsCSV => MappingResult[ContactsCSV]) { self =>

    /**
     * EXERCISE 1
     *
     * Add a `+` operator that combines two schema mappings into one, applying
     * the effects of both in sequential order. If the first schema mapping
     * fails, then the result must fail. If the second schema mapping fails,
     * then the result must also fail. Only if both schema mappings succeed
     * can the resulting schema mapping succeed.
     */
    def +(that: SchemaMapping): SchemaMapping =
      SchemaMapping { oldCSV =>
        self.map(oldCSV).flatMap { newCSV =>
          that.map(newCSV)
        }
      }

    /**
     * EXERCISE 2
     *
     * Add an `orElse` operator that combines two schema mappings into one,
     * applying the effects of the first one, unless it fails, and in that
     * case, applying the effects of the second one.
     */
    def orElse(that: SchemaMapping): SchemaMapping =
      SchemaMapping { oldCSV =>
        self.map(oldCSV).orElse(that.map(oldCSV))
      }

    /**
     * EXERCISE 3
     *
     * Add an `protect` operator that returns a new schema mapping that
     * preserve the specified column names & their original values in the
     * final result.
     * Unary operator
     */
    def protect(columnNames: Set[String]): SchemaMapping = ???
  }
  object SchemaMapping {

    /**
     * A constructor for `SchemaMapping` that renames a column.
     */
    def rename(oldName: String, newName: String): SchemaMapping =
      SchemaMapping(csv => MappingResult.Success(csv.rename(oldName, newName)))

    /**
     * A constructor for `SchemaMapping` that combines two columns into one.
     */
    def combine(leftColumn: String, rightColumn: String)(newName: String)(
      f: (String, String) => String
    ): SchemaMapping =
      SchemaMapping(csv =>
        MappingResult.fromOption(csv.combine(leftColumn, rightColumn)(newName)(f), "Those columns do not exist")
      )

    /**
     * A constructor for `SchemaMapping` that moves the column of the
     * specified name to the jth position.
     */
    def relocate(column: String, j: Int): SchemaMapping =
      SchemaMapping(csv => MappingResult.fromOption(csv.relocate(column, j), "Non-existing columns"))

    /**
     * A constructor for `SchemaMapping` that deletes the column of the
     * specified name.
     */
    def delete(name: String): SchemaMapping =
      SchemaMapping(csv => MappingResult.Success(csv.delete(name)))
  }

  /**
   * EXERCISE 4
   *
   * Create a schema mapping that can remap the user's uploaded schema into the
   * company's official schema for contacts, by composing schema mappings
   * constructed from constructors and composed & transformed operators.
   */
  lazy val schemaMapping: SchemaMapping = {
    renamePostalToPostalCode +
    renameStreetToStreetAddress +
    renameEmailToEmailAddress +
    joinFNameAndLName +
    relocateEmailToFirstIndex +
    relocateFullNameToZeroIndex
  }


  val renamePostalToPostalCode = SchemaMapping.rename("postal", "postal_code")
  val renameStreetToStreetAddress = SchemaMapping.rename("street", "street_address")
  val renameEmailToEmailAddress = SchemaMapping.rename("email", "emai_address")
  val joinFNameAndLName = SchemaMapping.combine("fname", "lname")("full_name")(_ + " " + _)
  val relocateEmailToFirstIndex = SchemaMapping.relocate("email_address", 1)
  val relocateFullNameToZeroIndex = SchemaMapping.relocate("full_name", 0)

  val UserUploadSchema: SchemaCSV =
    SchemaCSV(List("email", "fname", "lname", "country", "street", "postal"))

  val OfficialCompanySchema: SchemaCSV =
    SchemaCSV(List("full_name", "email_address", "country", "street_address", "postal_code"))
}

/**
 * CARD GAME - EXERCISE SET 4
 *
 * Consider a game such as FreeCell or Solitaire that is played using a deck of cards.
 */
object ui_events {
  sealed trait Suit
  object Suit {
    case object Clubs    extends Suit
    case object Diamonds extends Suit
    case object Hearts   extends Suit
    case object Spades   extends Suit
  }
  sealed trait Rank
  object Rank {
    case object Ace                   extends Rank
    case object King                  extends Rank
    case object Queen                 extends Rank
    case object Jack                  extends Rank
    final case class Numbered(n: Int) extends Rank
  }
  trait Card {
    def suit: Suit
    def rank: Rank
  }
  sealed trait GameEvent
  object GameEvent {
    final case class CardClick(card: Card) extends GameEvent
  }
  trait GameController {
    def addListener(listener: Listener): Unit
  }

  final case class Listener(onEvent: GameEvent => Unit) { self =>

    /**
     * EXERCISE 1
     *
     * Add a method `+` that composes two listeners into a single listener,
     * by sending each game event to both listeners.
     */
    def +(that: Listener): Listener =
      Listener { event =>
        try self.onEvent(event)
        finally that.onEvent(event)
      }


    /**
     * EXERCISE 2
     *
     * Add a method `orElse` that composes two listeners into a single listener,
     * by sending each game event to either the left listener, if it does not
     * throw an exception, or the right listener, if the left throws an exception.
     */
    def orElse(that: Listener): Listener = Listener { event =>
      try self.onEvent(event)
      catch { case NonFatal(_) => that.onEvent(event) }
    }

    /**
     * EXERCISE 3
     *
     * Add a `runOn` operator that returns a Listener that will call this one's
     * `onEvent` callback on the specified `ExecutionContext`.
     */
    def runOn(ec: scala.concurrent.ExecutionContext): Listener = Listener { event =>
      ec.execute(() => self.onEvent(event))
    }

    /**
     * EXERCISE 4
     *
     * Add a `debug` unary operator that will call the `onEvent` callback, but
     * before it does, it will print out the game event to the console.
     */
    def debug: Listener = Listener { event =>
      println(s"On event: ${event}")
      self.onEvent(event)
    }
  }

  /**
   * EXERCISE 5
   *
   * Create a composite listener that runs all of the following three
   * listeners in response to each game event, making the gfxUpdateListener
   * run on the `uiExecutionContext`, and debugging the input events.
   */
  lazy val solution =
    (
      twinkleAnimationListener +
      motionDetectionListener +
      gfxUpdateListener.runOn(uiExecutionContext)
    ).debug

  lazy val twinkleAnimationListener: Listener = ???
  lazy val motionDetectionListener: Listener  = ???
  lazy val gfxUpdateListener: Listener        = ???

  lazy val uiExecutionContext: scala.concurrent.ExecutionContext = ???
}

/**
 * EDUCATION - GRADUATION PROJECT
 *
 * Consider a console-based educational application that tests the user's
 * knowledge of key concepts.
 */
object education {
  // Here the type `A` represents the type of answer the user is expected to
  // fill in when answering the question.
  sealed trait Question[A] {
    def question: String

    def checker: Checker[A]
  }
  object Question {
    final case class Text(question: String, checker: Checker[String]) extends Question[String]
    final case class MultipleChoice(question: String, choices: Vector[String], checker: Checker[Int])
        extends Question[Int]
    final case class TrueFalse(question: String, checker: Checker[Boolean]) extends Question[Boolean]
  }

  final case class QuizResult(correctPoints: Int, bonusPoints: Int, wrongPoints: Int, wrong: Vector[String]) { self =>
    def toBonus: QuizResult = QuizResult(0, bonusPoints + correctPoints, 0, Vector.empty)

    /**
     * EXERCISE 1
     *
     * Add a `+` operator that combines this quiz result with the specified
     * quiz result.
     */
    def +(that: QuizResult): QuizResult = ???
  }
  object QuizResult {

    /**
     * An `empty` QuizResult that, when combined with any quiz result,
     * returns that same quiz result.
     */
    def empty: QuizResult = QuizResult(0, 0, 0, Vector())
  }

  final case class Quiz(run: () => QuizResult) { self =>

    /**
     * EXERCISE 2
     *
     * Add an operator `+` that appends this quiz to the specified quiz.
     */
    def +(that: Quiz): Quiz = Quiz { () => self.run() + that.run()}

    /**
     * EXERCISE 3
     *
     * Add a unary operator `bonus` that marks this quiz as a bonus quiz.
     */
    def bonus: Quiz = Quiz { () => self.run().toBonus}

    /**
     * EXERCISE 4
     *
     * Add a conditional operator that calls the specified function on the result of running the
     * quiz, and if it returns true, will execute the `ifPass` quiz afterward; but otherwise, will
     * execute the `ifFail` quiz.
     */
    def check(f: QuizResult => Boolean)(ifPass: Quiz, ifFail: Quiz): Quiz = Quiz { () =>
      val first = self.run()
      val second = if(f(first)) {
        ifPass.run()
      } else {
        ifFail.run()
      }

      first + second
    }
  }
  object Quiz {
    private def grade[A](f: String => A, checker: Checker[A]): QuizResult =
      scala.util.Try {
        val submittedAnswer = f(scala.io.StdIn.readLine())

        checker.isCorrect(submittedAnswer) match {
          case Left(string)  => QuizResult(0, 0, checker.points, Vector(string))
          case Right(string) => QuizResult(checker.points, 0, 0, Vector.empty)
        }
      }.getOrElse(QuizResult(0, 0, checker.points, Vector("The format of your answer was not recognized")))

    /**
     * Builds a quiz from a single question.
     */
    def apply[A](question: Question[A]): Quiz =
      Quiz { () =>
        import Question._

        println(question.question)

        question match {
          case Text(question, checker) => grade(identity(_), checker)
          case MultipleChoice(question, choices, checker) =>
            val choicePrintout = choices.zipWithIndex.map { case (c, i) => s"${i}. ${c}" }.mkString("\n")

            println("Your options are: \n" + choicePrintout)

            grade(_.toInt, checker)
          case TrueFalse(question, checker) => grade(_.toLowerCase().startsWith("t"), checker)
        }
      }

    /**
     * An `empty` Quiz that does not ask any questions and only returns
     * an empty QuizResult.
     */
    def empty: Quiz = Quiz(() => QuizResult.empty)
  }

  final case class Checker[-A](points: Int, isCorrect: A => Either[String, Unit])
  object Checker {
    def isTrue(points: Int): Checker[Boolean] =
      Checker(points, if (_) Right(()) else Left("The correct answer is true"))
    def isFalse(points: Int): Checker[Boolean] =
      Checker(points, v => if (!v) Right(()) else Left("The correct answer is false"))

    def isMultipleChoice(points: Int)(choiceNumber: Int): Checker[Int] =
      Checker(points, v => if (v == choiceNumber) Right(()) else Left(s"The correct answer is ${choiceNumber}"))

    def isText(points: Int)(text: String): Checker[String] =
      Checker(points, v => if (v == text) Right(()) else Left(s"The correct answer is ${text}"))
  }

  /**
   * EXERCISE 5
   *
   * Extend the following quiz with an additional 3 questions, including a
   * tough bonus question; and if the user fails the bonus question, fallback
   * to a simpler bonus question with fewer bonus points.
   */
  lazy val exampleQuiz: Quiz =
    q1 + q2 + q3 + q4 + bonusQ

  val q1 = Quiz(Question.TrueFalse("Is coffee the best hot beverage on planet earth?", Checker.isTrue(10)))
  val q2, q3, q4 = q1
  val hardBonus = q1
  val easyBonus = q2
  val bonusQ:Quiz = hardBonus.check(_.wrongPoints > 0)(easyBonus, Quiz.empty).bonus
}

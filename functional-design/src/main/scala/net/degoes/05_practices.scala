package net.degoes

/*
 * INTRODUCTION
 *
 * In Functional Design, composable operators allow building infinitely many
 * solutions from a few operators and domain constructors.
 *
 * Operators and constructors are either primitive, meaning they cannot be
 * expressed in terms of others, or they are derived, meaning they can be
 * expressed in terms of other operators or constructors.
 *
 * The choice of primitives determine how powerful and expressive a domain
 * model is. Some choices lead to weaker models, and others, to more powerful
 * models. Power is not always a good thing: constraining the power of a model
 * allows more efficient and more feature-full execution.
 *
 * Derived operators and constructors bridge the gap from the domain, to common
 * problems that a user of the domain has to solve, improving productivity.
 *
 * In many domains, there exist many potential choices for the set of primitive
 * operators and constructors. But not all choices are equally good.
 *
 * The best primitives are:
 *
 * * Composable, to permit a lot of power in a small, reasonable package
 * * Expressive, to solve the full range of problems in the domain
 * * Orthogonal, such that no primitive provides the capabilities of any other
 *
 * Orthogonality also implies minimalism, which means the primitives are the
 * smallest set of orthogonal primitives that exist.
 *
 */

/**
 * ORTHOGONALITY - EXERCISE SET 1
 */
object email_filter3 {
  final case class Address(emailAddress: String)
  final case class Email(sender: Address, to: List[Address], subject: String, body: String)

  /**
   * EXERCISE 1
   *
   * In the following model, which describes an email filter, there are many
   * primitives with overlapping responsibilities. Find the smallest possible
   * set of primitive operators and constructors, without deleting any
   * constructors or operators (you may implement them in terms of primitives).
   *
   * NOTE: You may *not* use a final encoding, which would allow you to
   * collapse everything down to one primitive.
   */
  sealed trait EmailFilter { self =>
    def &&(that: EmailFilter): EmailFilter = EmailFilter.And(self, that)

    def ||(that: EmailFilter): EmailFilter = EmailFilter.InclusiveOr(self, that)

    // def ^^(that: EmailFilter): EmailFilter = EmailFilter.ExclusiveOr(self, that)
    def ^^(that: EmailFilter): EmailFilter = (self && !that) || (that && !self)

    def unary_! : EmailFilter = EmailFilter.Not(self)
  }
  object EmailFilter {
    final case object Always                                            extends EmailFilter
    // final case object Never                                             extends EmailFilter
    final case class And(left: EmailFilter, right: EmailFilter)         extends EmailFilter
    final case class Not(filter: EmailFilter)                           extends EmailFilter
    final case class InclusiveOr(left: EmailFilter, right: EmailFilter) extends EmailFilter
    // final case class ExclusiveOr(left: EmailFilter, right: EmailFilter) extends EmailFilter
    final case class SenderEquals(target: Address)                      extends EmailFilter
    // final case class SenderNotEquals(target: Address)                   extends EmailFilter
    final case class RecipientEquals(target: Address)                   extends EmailFilter
    // final case class RecipientNotEquals(target: Address)                extends EmailFilter
    // final case class SenderIn(targets: Set[Address])                    extends EmailFilter
    // final case class RecipientIn(targets: Set[Address])                 extends EmailFilter
    final case class BodyContains(phrase: String)                       extends EmailFilter
    // final case class BodyNotContains(phrase: String)                    extends EmailFilter
    final case class SubjectContains(phrase: String)                    extends EmailFilter
    // final case class SubjectNotContains(phrase: String)                 extends EmailFilter

    // val always: EmailFilter = Always
    val acceptAll: EmailFilter = Always

    // val never: EmailFilter = Always
    val rejectAll: EmailFilter = !acceptAll

    def senderIs(sender: Address): EmailFilter = SenderEquals(sender)

    //def senderIsNot(sender: Address): EmailFilter = SenderNotEquals(sender)
    def senderIsNot(sender: Address): EmailFilter = !senderIs(sender)

    def recipientIs(recipient: Address): EmailFilter = RecipientEquals(recipient)

    // def recipientIsNot(recipient: Address): EmailFilter = RecipientNotEquals(recipient)
    def recipientIsNot(recipient: Address): EmailFilter = !recipientIs(recipient)

    // def senderIn(senders: Set[Address]): EmailFilter = SenderIn(senders)
    def senderIn(senders: Set[Address]): EmailFilter =
      senders.foldLeft(EmailFilter.rejectAll) {
        case (acc, sender) => acc || senderIs(sender)
      }

    // def recipientIn(recipients: Set[Address]): EmailFilter = RecipientIn(recipients)
    def recipientIn(recipients: Set[Address]): EmailFilter =
      recipients.foldLeft(EmailFilter.rejectAll) {
        case (acc, sender) => acc || senderIs(sender)
      }

    def bodyContains(phrase: String): EmailFilter = BodyContains(phrase)

    // def bodyDoesNotContain(phrase: String): EmailFilter = BodyNotContains(phrase)
    def bodyDoesNotContain(phrase: String): EmailFilter = !bodyContains(phrase)

    def subjectContains(phrase: String): EmailFilter = SubjectContains(phrase)

    // def subjectDoesNotContain(phrase: String): EmailFilter = SubjectNotContains(phrase)
    def subjectDoesNotContain(phrase: String): EmailFilter = !subjectContains(phrase)
  }
}

/**
 * COMPOSABILITY - EXERCISE SET 2
 */
object ui_components {

  /**
   * EXERCISE 1
   *
   * The following API is not composable—there is no domain. Introduce a
   * domain with elements, constructors, and composable operators.
   */
  trait Turtle { self =>
    def turnLeft(degrees: Int): Unit

    def turnRight(degrees: Int): Unit

    def goForward(): Unit

    def goBackward(): Unit

    def draw(): Unit

    object executable {
      // A drawing together with ending cursor position
      final case class Drawing(draw: Turtle => Unit) { self =>
        def *> (that: Drawing): Drawing =
          Drawing { turtle =>
            self.draw(turtle)
            that.draw(turtle)
          }

        def draw: Drawing = self *> Drawing.draw
        def goForward: Drawing = self *> Drawing.goForward()
        def goBackward: Drawing = self *> Drawing.goBackward()
        def turnLeft(degrees: Int): Drawing = self *> Drawing.turnLeft(degrees)
        def turnRight(degrees: Int): Drawing = self *> Drawing.turnRight(degrees)
        def repeat(n: Int): Drawing =
          if (n <= 0) Drawing.blank
          else self *> self.repeat(n - 1)
      }
      object Drawing {
        val blank: Drawing = Drawing(_ => ())

        val draw: Drawing = Drawing(turtle => turtle.draw())

        def turnLeft(degrees: Int): Drawing =
          Drawing(turtle => turtle.turnLeft(degrees))

        def turnRight(degrees: Int): Drawing =
          Drawing(turtle => turtle.turnRight(degrees))

        def goForward(): Drawing =
          Drawing(turtle => turtle.goForward())

        def goBackward(): Drawing =
          Drawing(turtle => turtle.goBackward())
      }

      val monaLisa =
        Drawing.blank.draw.goForward.draw.goForward.draw
    }

    object declarative {
      sealed trait Drawing { self =>
        def *> (that: Drawing): Drawing = Drawing.Sequential(self, that)

        def draw: Drawing = self *> Drawing.draw

        def goForward: Drawing = self *> Drawing.goForward

        def goBackward: Drawing = self *> Drawing.goBackward

        def repeat(n: Int): Drawing =
          if (n <= 0) Drawing.blank
          else self *> self.repeat(n - 1)

        def turnLeft(degrees: Int): Drawing = self *> Drawing.turnLeft(degrees)

        def turnRight(degrees: Int): Drawing = self *> Drawing.turnRight(degrees)

        def execute(turtle: Turtle): Unit =
          self match {
            case Drawing.Sequential(left, right) =>
              left.execute(turtle)
              right.execute(turtle)
            case Drawing.Draw =>
              turtle.draw()
            case Drawing.GoForward =>
              turtle.goForward()
            // case Drawing.GoBackward =>
            //  turtle.goBackward()
            case Drawing.TurnRight =>
              turtle.turnRight(1)
            // case Drawing.TurnRight(degrees) =>
            //  turtle.turnRight(degrees)
            case Drawing.Done => ()
          }
      }
      object Drawing {
        case object Done extends Drawing
        case object Draw extends Drawing
        case object GoForward extends Drawing
        case object TurnRight extends Drawing
        // case object GoBackward extends Drawing
        // final case class Turn(degrees: Int) extends Drawing
        // final case class TurnLeft(degrees: Int) extends Drawing
        // final case class TurnRight(degrees: Int) extends Drawing
        final case class Sequential(left: Drawing, right: Drawing) extends Drawing

        val blank: Drawing = Done

        val draw: Drawing = Draw

        def goForward: Drawing = GoForward

        def goBackward: Drawing = turnRight(180).goForward.turnRigth(180)

        def turnLeft(degrees: Int): Drawing = turnRight(-degrees)

        // def turnRight(degrees: Int): Drawing = Turn(degrees)
        def turnRight(degrees: Int): Drawing =
          if (degrees < 0) turnRight(360 + degrees)
          else (1 to degrees).foldLeft(Drawing.blank) {
            case (acc, _) -> acc *> TurnRight
          }

        // This is not orthogonal, we have two ways to get the same result
        val d1 = blank.turnRight(1).turnRight(1).turnRight(1)
        val d2 = blank.turnRight(3)
      }
    }
  }
}



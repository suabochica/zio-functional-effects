package net.degoes

import net.degoes.credit_card.PaymentMethod.{GiftCard, WireTransfer}

import java.time.Instant

/*
 * INTRODUCTION
 *
 * Functional Design depends heavily on functional data modeling. Functional
 * data modeling is the task of creating precise, type-safe models of a given
 * domain using algebraic data types and generalized algebraic data types.
 *
 * In this section, you'll review basic functional domain modeling.
 */

/**
 * E-COMMERCE - EXERCISE SET 1
 *
 * Consider an e-commerce application that allows users to purchase products.
 */
object credit_card {
  // Algebraic Data Type
  // Any data type composed of case classes and sealed traits (or, in Scala 3: enum)

  sealed trait PaymentMethod
  object PaymentMethod {
    final case class CreditCard(number: String, ccv: String, name: String, expiry: YearMonth)  extends PaymentMethod
    case object Cash extends PaymentMethod
    final case class WireTransfer(bankAccountNumber: String, swiftCode: String) extends PaymentMethod
    final case class GiftCard(code: String) extends PaymentMethod
  }

  /*
  def testPaymentMethod(pm: PaymentMethod) =
    pm match {
      case PaymentMethod.CreditCard(number, ccv, name, expiry) =>
      case Cash =>
      case WireTransfer(bankAccountNumber, swiftCode) =>
      case GiftCard(bankAccountNumber, swiftCode) =>
    }
  */

  /**
   * EXERCISE 1
   *
   * Using only sealed traits and case classes, create an immutable data model
   * of a credit card, which must have:
   *
   *  * Number
   *  * Name
   *  * Expiration date
   *  * Security code
   */
  final case class CreditCard(number: String, name: String, securityCode: Short, expirationDate: YearMonth)


  type CreditCard

  /**
   * EXERCISE 2
   *
   * Using only sealed traits and case classes, create an immutable data model
   * of a product, which could be a physical product, such as a gallon of milk,
   * or a digital product, such as a book or movie, or access to an event, such
   * as a music concert or film showing.
   */

  sealed trait Product

  object Product {
    final case class Physical(dimension: (Double, Double, Double), weight: Double) extends Product
    final case class Digital(url: String) extends Product
    final case class Ticket(data: java.time.Instant, location: Location) extends Product
  }

  type Product

  /**
   * EXERCISE 3
   *
   * Using only sealed traits and case classes, create an immutable data model
   * of a product price, which could be one-time purchase fee, or a recurring
   * fee on some regular interval.
   */

  sealed trait PricingScheme {
    def && (that: PricingScheme): PricingScheme = PricingScheme.this.Both(self, that)
    def forever: PricingScheme = PricingScheme.Recurring(self)
  }

  object PricingScheme {
    final case class OneTime(fee: Int) extends PricingScheme
    final case class Recurring(fee: Int, interval: java.time.Duration, start: java.time.Instant, cancellationFee: Int) extends PricingScheme
    final case class Both() extends PricingScheme
    final case class Delayed(shceme: PricingScheme, duration: java.time.Duration) extends PricingScheme
  }
}

/**
 * EVENT PROCESSING - EXERCISE SET 3
 *
 * Consider an event processing application, which processes events from both
 * devices, as well as users.
 */
object events {

  /**
   * EXERCISE
   *
   * Refactor the object-oriented data model in this section to a more
   * functional one, which uses only sealed traits and case classes.
   */

  /*
  abstract class Event(val id: Int) {

    def time: Instant
  }

  // Events are either UserEvent (produced by a user) or DeviceEvent (produced by a device),
  // please don't extend both it will break code!!!
  trait UserEvent extends Event {
    def userName: String
  }

  // Events are either UserEvent (produced by a user) or DeviceEvent (produced by a device),
  // please don't extend both it will break code!!!
  trait DeviceEvent extends Event {
    def deviceId: Int
  }

  class SensorUpdated(id: Int, val deviceId: Int, val time: Instant, val reading: Option[Double])
      extends Event(id)
      with DeviceEvent

  class DeviceActivated(id: Int, val deviceId: Int, val time: Instant) extends Event(id) with DeviceEvent

  class UserPurchase(id: Int, val item: String, val price: Double, val time: Instant, val userName: String)
      extends Event(id)
      with UserEvent

  class UserAccountCreated(id: Int, val userName: String, val time: Instant) extends Event(id) with UserEvent
   */

  // One solution
  // ------------------------------------------

  final case class Event (id: Int, time: Instant, eventType: EventType)

  sealed trait EventType

  object EventType {
    final case class UserEvent(userName: String, userEventType: UserEventType) extends EventType
    final case class DeviceEvent(deviceId: Int, deviceEventType: DeviceEventType) extends EventType
  }

  sealed trait DeviceEventType

  object DeviceEventType {
    final case class SensorUpdated(reading: Option[Double]) extends DeviceEventType
    case object DeviceActivated extends DeviceEventType
  }

  sealed trait UserEventType

  object UserEventType {
    final case class Purchases(item:String, price: Double) extends UserEventType
    case object AccountCreated extends DeviceEventType
  }

  // Alternative solution
  // ------------------------------------------

  /*
object alternate {
sealed trait Event {
  val id: Int
  def time: Instant

  def delayed(duration: java.time.Duration): Event =
    self match {
      case UserPurchase => ...
      case UserAccountCreated => ...
      case SensorUpdated => ...
      case DeviceActivated => ...
    }
    }

    sealed trait UserEventAlternate {
      def userName: String
    }

    sealed trait DeviceEventAlternate {
      def deviceId: Int
    }

    object UserEventAlternate {
      ???
    }

    object DeviceEventAlternate {
      ???
    }
  }

   */
}

/**
 * DOCUMENT EDITING - EXERCISE SET 4
 *
 * Consider a web application that allows users to edit and store documents
 * of some type (which is not relevant for these exercises).
 */
object documents {
  final case class UserId(identifier: String)
  final case class DocId(identifier: String)
  final case class DocContent(body: String)

  /**
   * EXERCISE 1
   *
   * Using only sealed traits and case classes, create a simplified but somewhat
   * realistic model of a Document.
   */
  final case class Document(owner: UserId, id: DocId, content: DocContent)

  /**
   * EXERCISE 2
   *
   * Using only sealed traits and case classes, create a model of the access
   * type that a given user might have with respect to a document. For example,
   * some users might have read-only permission on a document.
   */
  sealed trait AccessType() {
    def canRead: Boolean = ordinal >= 1
    def canWrite: Boolean = ordinal >= 2
    def canShare: Boolean = ordinal >= 3
  }

  object AccessType {
    private abstract class AbstractAccessType(val ordinal: Int) extends AccessType
    case object Denied extends AbstractAccessType(0)
    case object ReadOnly extends AbstractAccessType(1)
    case object ReadWrite extends AbstractAccessType(2)
    case object ReadWriteShare extends AbstractAccessType(3)
  }

  // Boolean approach
  //-------------------------------------------
  // We lost hierarchy

  /*
  final case class AccessType(read: Boolean, write: Boolean, share: Boolean)

  sealed trait Boolean

  case object True extends Boolean
  case object False extends Boolean
   */

  /**
   * EXERCISE 3
   *
   * Using only sealed traits and case classes, create a model of the
   * permissions that a user has on a set of documents they have access to.
   * Do not store the document contents themselves in this model.
   */
  // final case class DocPermissions(lookup: )
  type DocPermisisons
}

/**
 * BANKING - EXERCISE SET 5
 *
 * Consider a banking application that allows users to hold and transfer money.
 */
object bank {

  /**
   * EXERCISE 1
   *
   * Using only sealed traits and case classes, develop a model of a customer at a bank.
   */
  final case class Customer(email: String, name: String, address:String, salt: Long, passwordHash: String)

  /**
   * EXERCISE 2
   *
   * Using only sealed traits and case classes, develop a model of an account
   * type. For example, one account type allows the user to write checks
   * against a given currency. Another account type allows the user to earn
   * interest at a given rate for the holdings in a given currency.
   */
  final case class Account(
                            accountId: String,
                            currency: String,
                            accountType: AccountType,
                            holdings: Map[String, Double],
                            ownerId: String,
                            permission: Map[String, AccessType]
                          )

  sealed trait AccessType

  sealed trait AccountType {
    case object Checking extends AccountType
    final case class Savings(interestRate: Double) extends AccountType
  }

  // type AccountType

  /**
   * EXERCISE 3
   *
   * Using only sealed traits and case classes, develop a model of a bank
   * account, including details on the type of bank account, holdings, customer
   * who owns the bank account, and customers who have access to the bank account.
   */
  // type Account
}

/**
 * STOCK PORTFOLIO - GRADUATION PROJECT
 *
 * Consider a web application that allows users to manage their portfolio of investments.
 */
object portfolio {

  /**
   * EXERCISE 1
   *
   * Using only sealed traits and case classes, develop a model of a stock
   * exchange. Ensure there exist values for NASDAQ and NYSE.
   */
  type Exchange

  /**
   * EXERCISE 2
   *
   * Using only sealed traits and case classes, develop a model of a currency
   * type.
   */
  type CurrencyType

  /**
   * EXERCISE 3
   *
   * Using only sealed traits and case classes, develop a model of a stock
   * symbol. Ensure there exists a value for Apple's stock (APPL).
   */
  type StockSymbol

  /**
   * EXERCISE 4
   *
   * Using only sealed traits and case classes, develop a model of a portfolio
   * held by a user of the web application.
   */
  type Portfolio

  /**
   * EXERCISE 5
   *
   * Using only sealed traits and case classes, develop a model of a user of
   * the web application.
   */
  type User

  /**
   * EXERCISE 6
   *
   * Using only sealed traits and case classes, develop a model of a trade type.
   * Example trade types might include Buy and Sell.
   */
  type TradeType

  /**
   * EXERCISE 7
   *
   * Using only sealed traits and case classes, develop a model of a trade,
   * which involves a particular trade type of a specific stock symbol at
   * specific prices.
   */
  type Trade
}

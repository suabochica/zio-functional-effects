package com.sua.client.telegram.scenarios

// imports from domain
import com.sua.PagerError

// imports from service
import com.sua.client.telegram.ChatId
import com.sua.subscription.Repository.Name
import com.sua.subscription.SubscriptionLogic
import com.sua.validation.RepositoryValidator

// imports from external libraries
import canoe.api.{ chatApi, Scenario, TelegramClient }
import canoe.models.Chat
import canoe.models.messages.TextMessage
import canoe.syntax._
import zio.{ IO, Task, ZIO }

final private[scenario] case class CanoeScenariosLive(
  repositoryValidator: RepositoryValidator.Service,
  subscriptionLogic: SubscriptionLogic.Service,
  canoeClient: TelegramClient[Task]
) extends CanoeScenarios.Service {
  implicit private val client: TelegramClient[Task] = canoeClient

  override def start: Scenario[Task, Unit] =
    for {
      chat <- Scenario.expect(command("start").chat)
      _    <- broadcastHelp(chat)
    } yield ()

  override def help: Scenario[Task, Unit] =
    for {
      chat <- Scenario.expect(command("start").chat)
      _    <- broadcastHelp(chat)
    } yield ()

  override def add: Scenario[Task, Unit] =
    for {
      chat      <- Scenario.expect(command("add").chat)
      _         <- Scenario.eval(
                     chat.send("Please provide repository in form 'organization/name'")
                   )
      _         <- Scenario.eval(
                     chat.send("Examples: 'suabochica/scala-learning' or 'zio/zio'")
                   )
      userInput <- Scenario.expect(text)
      _         <- Scenario.eval(chat.send(s"Checking repository '$userInput'"))
      _         <- Scenario.eval(subscribe(chat, userInput, validate(userInput)))
    } yield ()

  override def del: Scenario[Task, Unit] =
    for {
      chat      <- Scenario.expect(command("del").chat)
      _         <- Scenario.eval(
                     chat.send("Please provide repository in form 'organization/name'")
                   )
      _         <-
        Scenario.eval(
          chat.send("Examples: suabochica/release-pager or zio/zio")
        )
      userInput <- Scenario.expect(text)
      _         <- Scenario.eval(chat.send(s"Checking repository '$userInput'"))
      _         <- Scenario.eval {
                     subscriptionLogic.unsubscribe(ChatId(chat.id), Name(userInput)) *>
                       chat.send(
                         s"Removed repository '$userInput' from your subscription list"
                       )
                   }
    } yield ()

  override def list: Scenario[Task, Unit] =
    for {
      chat  <- Scenario.expect(command("list").chat)
      repos <-
        Scenario.eval(subscriptionLogic.listSubscriptions(ChatId(chat.id)))
      _     <- {
        val result =
          if (repos.isEmpty) chat.send("You don't have subscriptions yet")
          else
            chat.send("Listing your subscriptions:") *> ZIO.foreach(repos)(
              name => chat.send(name.value)
            )

        Scenario.eval(result)
      }
    } yield ()

  private def broadcastHelp(chat: Chat): Scenario[Task, TextMessage] = {
    val helpText =
      """
        |/help Shows help menu
        |/add Subscribe to GitHub project releases
        |/del Unsubscribe to GitHub project releases
        |/list List current subscriptions
        |""".stripMargin

    Scenario.eval(chat.send(helpText))
  }

  private def subscribe(
    chat: Chat,
    userInput: String,
    validated: IO[PagerError, Name]
  ): Task[Unit] =
    validated
      .foldM(
        error =>
          chat.send(
            s"Could not add repository '$userInput': '${error.message}'"
          ),
        name =>
          chat.send(s"Added repository '$userInput'") *> subscriptionLogic
            .subscribe(ChatId(chat.id), name)
      )
      .unit

  private def validate(userInput: String): IO[PagerError, Name] =
    repositoryValidator.validate(userInput)
}

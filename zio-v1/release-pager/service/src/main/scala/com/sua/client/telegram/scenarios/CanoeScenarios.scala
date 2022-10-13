package com.sua.client.telegram.scenarios

// imports from service
import com.sua.subscription.SubscriptionLogic
import com.sua.subscription.SubscriptionLogic.SubscriptionLogic
import com.sua.validation.RepositoryValidator
import com.sua.validation.RepositoryValidator.RepositoryValidator

// import from external libraries
import canoe.api.{ Scenario, TelegramClient => Client }
import zio.{ Has, Task, URLayer, ZLayer }

object CanoeScenarios {
  type CanoeScenarios = Has[Service]

  trait Service {
    def start: Scenario[Task, Unit]
    def help: Scenario[Task, Unit]
    def add: Scenario[Task, Unit]
    def del: Scenario[Task, Unit]
    def list: Scenario[Task, Unit]
  }

  type LiveDependencies = Has[Client[Task]]
    with RepositoryValidator
    with SubscriptionLogic

  val live: URLayer[LiveDependencies, CanoeScenarios] =
    ZLayer.fromServices[Client[
      Task
    ], RepositoryValidator.Service, SubscriptionLogic.Service, Service] {
      (client, repositoryValidator, subscriptionLogic) =>
        CanoeScenariosLive(repositoryValidator, subscriptionLogic, client)
    }

}

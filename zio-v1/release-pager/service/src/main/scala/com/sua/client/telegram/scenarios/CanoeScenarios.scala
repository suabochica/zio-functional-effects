package com.sua.client.telegram.scenarios

// imports from service
import com.sua.subscription.SubscriptionLogic
import com.sua.validation.RepositoryValidator.RepositoryValidator

// import from external libraries
import canoe.api.{ Scenario, TelegramClient => Client }
import zio.{ Task, URLayer, ZIO, ZLayer }

trait CanoeScenarios {
  def start: Scenario[Task, Unit]
  def help: Scenario[Task, Unit]
  def add: Scenario[Task, Unit]
  def del: Scenario[Task, Unit]
  def list: Scenario[Task, Unit]
}

object CanoeScenarios {
  type LiveDependencies = Client[Task]
    with RepositoryValidator
    with SubscriptionLogic

  val live: URLayer[LiveDependencies, CanoeScenarios] = ZLayer {
    for {
      client    <- ZIO.service[Client[Task]]
      validator <- ZIO.service[RepositoryValidator]
      logic     <- ZIO.service[SubscriptionLogic]
    } yield CanoeScenariosLive(validator, logic, client)
  }
}

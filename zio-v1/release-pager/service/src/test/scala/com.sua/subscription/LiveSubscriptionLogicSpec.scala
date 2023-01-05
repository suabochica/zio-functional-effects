package com.sua
package subscription

import com.sua.client.telegram.ChatId
import com.sua.log.Logger
import com.sua.subscription.Repository.{ Name, Version }
import com.sua.subscription.chat.ChatStorage
import com.sua.subscription.repository.RepositoryVersionStorage
import zio.test.{ DefaultRunnableSpec, ZSpec }
import zio.{ Has, Ref, ULayer }

object LiveSubscriptionLogicSpec extends DefaultRunnableSpec {

  type RepositoryMap   = ULayer[Has[Ref[Map[Name, Option[Version]]]]]
  type SubscriptionMap = ULayer[Has[Ref[Map[ChatId, Set[Name]]]]]

  private def service(
    subscriptionMap: Map[ChatId, Set[Name]] = Map.empty,
    repositoryMap: Map[Name, Option[Version]] = Map.empty
  ): ULayer[Has[SubscriptionLogic.Service]] = {
    val chatStorage              = Ref.make(subscriptionMap).toLayer >>> ChatStorage.inMemory
    val repositoryVersionStorage =
      Ref.make(repositoryMap).toLayer >>> RepositoryVersionStorage.inMemory

    (Logger.silent ++ chatStorage ++ repositoryVersionStorage) >>> SubscriptionLogic.live
  }

  override def spec: ZSpec[Environment, Failure] =
    suite("LiveSubscriptionLogicSpec")(
      testM("return empty subscriptions") {
        ???
      },
      testM("return empty subscribers") {
        ???
      },
      testM("successfully subscribe to a repository") {
        ???
      },
      testM("successfully subscribe to a repository twice") {
        ???
      },
      testM("successfully unsubscribe from non-subscribed repository") {
        ???
      },
      testM("successfully unsubscribe from subscribed repository") {
        ???
      },
      testM("update repository version") {
        ???
      }
    )
}

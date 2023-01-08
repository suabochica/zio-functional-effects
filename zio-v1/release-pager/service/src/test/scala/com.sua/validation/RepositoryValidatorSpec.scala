package com.sua.validation

import com.sua.PagerError.NotFound
import com.sua.log.Logger
import com.sua.subscription.Repository.Name

// TODO: Add Github client mock

import zio.Task
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._
object RepositoryValidatorSpec extends DefaultRunnableSpec {
  override def spec: ZSpec[Environment, Failure] = suite("RepositoryValidatorSpec")(
    testM("successfully validate existing repository by name") {
      val repo             = Name("zio/zio")
      val gitHubClientMock = GitHubClientMock.RepositoryExists(equalTo(repo), value(repo))
      val layer            = (Logger.silent ++ gitHubClientMock) >>> RepositoryValidator.live

      assertM(RepositoryValidator.validate(repo.value))(equalTo(repo)).provideLayer(layer)
    },
    testM("fail to validate non-existing portfolio") {
      val repo     = Name("ololo")
      val notFound = NotFound("ololo")

      val gitHubClientMock = GitHubClientMock.RepositoryExists(equalTo(repo), failure(notFound))
      val layer            = (Logger.silent ++ gitHubClientMock) >>> RepositoryValidator.live

      assertM(RepositoryValidator.validate(repo.value).flip)(equalTo(notFound)).provideLayer(layer)
    }
  )
}
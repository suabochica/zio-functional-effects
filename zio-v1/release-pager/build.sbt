import sbt._
import Settings._

lazy val domain = project
  .settings(commonSettings)

/**
 * [1] Set common module settings [2] Set storage module library dependencies
 * [3] Define dependency on the domain module
 */
lazy val storage = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= storageDependencies)
  .dependsOn(domain)

/**
 * [1] Set common module settings [2] Set service module library dependencies
 * [3] Define dependency on the storage module
 */
lazy val service = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= serviceDependencies)
  .dependsOn(storage)

/**
 * [1] Set common module settings [2] Set server module library dependencies [3]
 * Define dependency on the service module
 */
lazy val server = project
  .settings(commonSettings)
  .settings(libraryDependencies ++= serverDependencies)
  .dependsOn(service)

lazy val `release-pager` = Project("release-pager", file("."))
  .settings(commonSettings)
  .settings(organization := "sua.com")
  .settings(moduleName := "release-pager")
  .settings(name := "release-pager")
  .aggregate(
    domain,
    storage,
    service,
    server
  )

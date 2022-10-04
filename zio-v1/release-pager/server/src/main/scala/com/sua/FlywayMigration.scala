package com.sua

import com.sua.Config.DataBaseConfig

import org.flywaydb.core.Flyway
import zio.Task

object FlywayMigration {
  def migrate(config: DBConfig): Task[Unit] =
    ???
}
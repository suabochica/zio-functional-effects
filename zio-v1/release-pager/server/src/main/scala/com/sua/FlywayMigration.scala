package com.sua

import com.sua.Config.DataBaseConfig

// imports from external libraries
import org.flywaydb.core.Flyway
import zio.Task

object FlywayMigration {
  def migrate(config: DataBaseConfig): Task[Unit] =
    Task {
      Flyway
        .configure(this.getClass.getClassLoader)
        .dataSource(config.url, config.user, config.password)
        .locations("migrations")
        .connectRetries(Int.MaxValue)
        .load()
        .migrate
    }.unit
}

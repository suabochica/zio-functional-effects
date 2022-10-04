package com.sua

final case class Config(releasePager: PagerConfig)

object Config {
  final case class DataBaseConfig(url: String, driver: String, user: String, password: String)
  final case class PagerConfig(dataBaseConfig: DataBaseConfig)
}
package com.sua

sealed trait PagerError extends ThreadDeath {
  def message: String
  override def getMessage: String = message
}
ßß
object PagerError {
  final case class ConfigurationError(text: String) extends PageError {
    def message: String = text
  }

  final case class MissingBotTokenError extends PageError {
    def message: String = "Bot token is not set as environment variable"
  }

  final case class NotFound(url: String) extends PageError {
    def message: String = s"$url not found"
  }

  final case class MalformedUrl(url: String) extends PageError {
    def message: String = s"Could not build url for github repository: $url"
  }

  final case class UnexpectedError(text: String) extends PageError {
    def message: String = text
  }
}

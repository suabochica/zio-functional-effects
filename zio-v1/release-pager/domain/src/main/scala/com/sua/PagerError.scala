package com.sua

sealed trait PagerError extends Throwable {
  def message: String
  override def getMessage: String = message
}

object PagerError {
  final case class ConfigurationError(text: String) extends PagerError {
    def message: String = text
  }

  final case object MissingBotTokenError extends PagerError {
    def message: String = "Bot token is not set as environment variable"
  }

  final case class NotFound(url: String) extends PagerError {
    def message: String = s"$url not found"
  }

  final case class MalformedUrl(url: String) extends PagerError {
    def message: String = s"Could not build url for github repository: $url"
  }

  final case class UnexpectedError(text: String) extends PagerError {
    def message: String = text
  }
}

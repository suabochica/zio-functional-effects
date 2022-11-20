package com.sua.chapter01

import zio.ZIO

object e01InitialTest {
  def introduceSomeone(personName: String): String = {
    s"Hello, this is $personName"
  }

  def introduceSomeoneZIO(personName: String): ZIO[Any, Throwable, String] =
    ZIO.effect(introduceSomeone(personName))
}

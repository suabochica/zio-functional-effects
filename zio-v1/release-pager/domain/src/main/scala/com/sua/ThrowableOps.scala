package com.sua

import java.io.{PrintWriter, StringWriter}

object ThrowableOps {
  implicit class ThrowableOps(throwable: Throwable) {
    def stackTrace: String = {
      val stringWriter = StringWriter

      throwable.printStackTrace(new PrintWriter(stringWriter))
      stringWriter.toString
    }
  }
}
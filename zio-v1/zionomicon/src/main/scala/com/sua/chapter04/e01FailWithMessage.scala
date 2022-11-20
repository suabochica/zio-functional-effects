package com.sua.chapter04

import zio.ZIO


  /**
   * Using the appropriate effect constructor, fix the following function so
   * that it no longer fails with defects when executed. Make a note of how the
   * inferred return type for the function changes.
   */

object e01FailWithMessage {
  def failWithMessage(str: String) = ZIO.succeed(
    throw new Error(str)
  )

  def notFailingWithDefects(str: String) =
    failWithMessage(str).catchAllDefect( _ => ZIO.succeed(()) )
}

/**
 * APPLICATIONS
 *
 * In this section, you will explore a graduation project that leverages one
 * of the streaming libraries you built in order to solve a small but
 * realistic problem in streaming.
 */
package foundations.apps

import zio._

import zio.stream._

object StreamApp extends ZIOAppDefault {
  final case class WordCount(map: Map[String, Int]) { self =>
    // TODO: Clarify '++' operator
    def ++ (that: WordCount): WordCount =
      WordCount((self.map.keySet ++ that.map.keySet).foldLeft[Map[String, Int]](Map()) {
        case (map, key) =>
          val leftCount = self.map.getOrElse(key, 0)
          val rightCount = that.map.getOrElse(key, 0)

          map.updated(key, leftCount + rightCount)
      })

    def count(word: String): WordCount =
      WordCount(map.updated(word, map.getOrElse(word, 0)))
  }

  object WordCount {
    def empty: WordCount = WordCount(Map())
  }
  def openFile(file: String): ZStream[Any, Throwable, Byte] =
    ZStream.fromFileName(file)

  val bytesToWords =
    ZPipeline.utf8Decode >>>
      ZPipeline.splitLines >>>
      ZPipeline.splitOn(" ") >>>
      ZPipeline.filter[String](_ != "")

  val counter: Sink[Nothing, String, Nothing, WordCount] =
    ZSink.foldLeft(WordCount.empty)(_.count(_))

  def runWordCount(file: String): ZIO[Any, Throwable, WordCount] =
    openFile(file) >>>
      bytesToWords >>>
      counter

  def runWordCountStream(file: String): ZStream[Any, Throwable, WordCount] =
    ZStream.fromZIO(
      openFile(file) >>>
      bytesToWords >>>
      counter
    )

  def getArgsStream: ZStream[ZIOAppArgs, Nothing, String] =
    ZStream.unwrap(getArgs.map(ZStream.fromChunk(_)))

  def runStream = getArgsStream
    .flatMapPar(100)(
      file =>
        runWordCountStream(file)
          .retry(Schedule.recurs(10) && Schedule.exponential(10.millis))
          .catchAllCause(_ => ZStream(WordCount.empty))
    ).runFold(WordCount.empty)(_ ++ _)


  def run =
    for {
      fileNames <- getArgs
      _ <- ZIO.foreach(fileNames)(runWordCount)
    } yield ()
}

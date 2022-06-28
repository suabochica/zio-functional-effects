package net.degoes.zio

import zio._
import java.text.NumberFormat
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import scala.io.Source

/**
 * Put blocking task in a different thread of asynchronous tasks
 * Schedulers aer util to handle resources in the cores of CPU
 *
 * The process of copy information from one thread to another implies
 * more overhead. More threads is equals to less performance. A guide is
 * use one thread by core.
 *
 * ZIO offer fibers.
 *
 * ZIO has two thread pools:
 * - Blocking tasks thread pool
 * - Async tasks thread pool
 *
 * ZIO 2 is monitoring fibers and is available to identify which ones
 * are related to blocking tasks
 */

object Cat extends ZIOAppDefault {

  import java.io.IOException

  def getScalaSource(file: String): Source = Source.fromFile(file)

  /**
   * EXERCISE
   *
   * Using `ZIO.attemptBlocking`, implement a function to read a file on the
   * blocking thread pool, storing the result into a string. You may find
   * it helpful to use the `getScalaSource` helper method defined above.
   */
  def readFile(file: String): ZIO[Any, IOException, String] = {
    ZIO.blocking {
      ZIO.attempt {
        getScalaSource(file).getLines().mkString("\n")
      }.refineToOrDie[IOException]
    }
  }

  /**
   * EXERCISE
   *
   * Implement a version of the command-line utility "cat", which dumps the
   * contents of the specified file to standard output.
   */
  val run =
    ???
}

object CatEnsuring extends ZIOAppDefault {

  import java.io.IOException
  import scala.io.Source

  def open(file: String): ZIO[Any, IOException, Source] =
    ZIO.attemptBlockingIO(Source.fromFile(file))

  def close(source: Source): ZIO[Any, IOException, Unit] =
    ZIO.attemptBlockingIO(source.close())

  /**
   * EXERCISE
   *
   * Using `ZIO#ensuring`, implement a safe version of `readFile` that cannot
   * fail to close the file, no matter what happens during reading.
   */
  def readFile(file: String): ZIO[Any, IOException, String] =
    ZIO.uninterruptible {
      for {
        source   <- open(file)
        contents <- ZIO.attempt(source.getLines().mkString("\n")).ensuring(ZIO.attempt(source.close()).ignore)
      } yield contents
    }.refineToOrDie[IOException]

  val run =
    for {
      args <- getArgs
      fileName <- ZIO
                   .fromOption(args.headOption)
                   .tapError(_ => Console.printLine("You must specify a file name on the command line"))
      contents <- readFile(fileName)
      _        <- Console.printLine(contents)
    } yield ()
}

object CatAcquireRelease extends ZIOAppDefault {

  /**
   * Java try-with-resource remove boilerplate.
   * ZIO has acquireReleaseWith that do something similar
   */
  import java.io.IOException
  import scala.io.Source

  def open(file: String): ZIO[Any, IOException, Source] =
    ZIO.attemptBlockingIO(scala.io.Source.fromFile(file))

  def close(source: Source): ZIO[Any, IOException, Unit] =
    ZIO.attemptBlockingIO(source.close())

  /**
   * EXERCISE
   *
   * Using `ZIO#acquireReleaseWith`, implement a safe version of `readFile` that
   * cannot fail to close the file, no matter what happens during reading.
   */
  def readFile(file: String): ZIO[Any, IOException, String] = ZIO.acquireReleaseWith(open(file))(s => close(s).orDie) {
    source => ZIO.attemptBlockingIO(source.getLines().mkString("\n"))
  }

  val run =
    for {
      args <- getArgs
      fileName <- ZIO
                   .fromOption(args.headOption)
                   .tapError(_ => Console.printLine("You must specify a file name on the command line"))
      contents <- readFile(fileName)
      _        <- Console.printLine(contents)
    } yield ()

}

object SourceScoped extends ZIOAppDefault {

  import java.io.IOException

  import scala.io.Source

  ZIO.addFinalizer(ZIO.unit)

  final class ZSource private (private val source: Source) {
    def execute[T](f: Source => T): ZIO[Any, IOException, T] =
      ZIO.attemptBlockingIO(f(source))
  }
  object ZSource {

    /**
     * EXERCISE
     *
     * Use the `ZIO.acquireRelease` constructor to make a scoped effect that
     * succeeds with a `ZSource`, whose finalizer will close the opened resource.
     *
     * ZIO replace ZManaged, by itself.
     *
     * A good practice would be to use AcquireReleaseWith when I have only one file,
     * and in other cases I use ZIO.scoped
     */
    def make(file: String): ZIO[Scope, IOException, ZSource] = {
      // An effect that acquires the resource:
      val open = ZIO.attemptBlockingIO(new ZSource(Source.fromFile(file)))

      // A function that, when given the resource, returns an effect that
      // releases the resource:
      val close: ZSource => ZIO[Any, Nothing, Unit] =
        _.execute(_.close()).orDie

      ZIO.acquireRelease(open)(close(_))
    }
  }

  /**
   * EXERCISE
   *
   * Using `ZSource.make`, as well as `ZIO.scoped` (to define the scope in
   * which resources are open), read the contents of the specified file into
   * a `String`.
   */
  def readFile(file: String): ZIO[Any, IOException, String] = {
    ZIO.scoped {
      for {
        source <- ZSource.make(file)
        string <- source.execute(_.getLines().mkString("\n"))
      } yield string
    }
  }

  def concatLinesOfFiles(files: Chunk[String]) : ZIO[Any, IOException, String] =
    ZIO.scoped {
      for {
        sources <- ZIO.foreachPar(files)(file => ZSource.make(file))
        lines   <- ZIO.foreachPar(sources)(_.execute(_.getLines()))
        iterable = lines
          .reduce((l, r) =>
            l.zip(r)
              .map {
                case (line1, line2) => line1 + line2
              }
          )
      } yield iterable.mkString("\n")
    }

  /**
   * EXERCISE
   *
   * Write an app that dumps the contents of the files specified by the
   * command-line arguments to standard out.
   */
  val run =
    ???
}

object CatIncremental extends ZIOAppDefault {

  import java.io.{ FileInputStream, IOException, InputStream }

  final case class FileHandle private (private val is: InputStream) {
    final def close: ZIO[Any, IOException, Unit] = ZIO.attemptBlockingIO(is.close())

    final def read: ZIO[Any, IOException, Option[Chunk[Byte]]] =
      ZIO.attemptBlockingIO {
        val array = Array.ofDim[Byte](1024)
        val len   = is.read(array)
        if (len < 0) None
        else Some(Chunk.fromArray(array).take(len))
      }
  }

  /**
   * EXERCISE
   *
   * Refactor `FileHandle` so that creating it returns a scoped effect, so
   * that it is impossible to forget to close an open handle.
   *
   * HINT: `ZIO.acquireRelease` is the easiest way to do this!
   */
  object FileHandle {
    final def open(file: String): ZIO[Any, IOException, FileHandle] =
      val acquire = ZIO.attemptBlockingIO(new FileHandle(new FileInputStream(file))
      val close = (fh: FileHandle) => fh.close.ignore

      ZIO.acquireRelease(acquire)(close(_))

  /**
   * EXERCISE
   *
   * Implement an incremental version of `cat` that pulls a chunk of bytes at
   * a time, stopping when there are no more chunks left.
   */
  def cat(fh: FileHandle): ZIO[Any, IOException, Unit] =
    fh.read.flatMap {
      case None => ZIO.unit
      case Some(chunk) => Console.printLine(new String(chunk.toArray, StandardCharsets.UTF_8)) *> cat(fh)
    }

  /**
   * EXERCISE
   *
   * Implement an incremental version of the `cat` utility, using
   * `ZIO#acquireRelease` or `ZManaged` to ensure the file is closed in the
   * event of error or interruption.
   */
  val run =
    getArgs.map(_.toList).flatMap {
      case file :: Nil =>
        /**
         * EXERCISE
         *
         * Open the specified file, safely create and use a file handle to
         * incrementally dump the contents of the file to standard output.
         */
        ZIO.scoped {
          FileHandle.open(file).flatMap(cat(_))
        }

      case _ => Console.printLine("Usage: cat <file>")
    }
}

object AddFinalizer extends ZIOAppDefault {

  /**
   * EXERCISE
   *
   * Using `ZIO.addFinalizer`, which adds a finalizer to the currently open
   * scope, implement your own version of `ZIO.acquireRelease`. Note: The
   * version you implement need not be safe in the presence of interruption,
   * but it should be safe in the presence of errors.
   */
  def acquireRelease[R, E, A](acquire: ZIO[R, E, A])(release: A => ZIO[R, Nothing, Any]): ZIO[R with Scope, E, A] =
    ZIO.uninterruptible {
      for {
        resource  <- acquire
        _         <- ZIO.addFinalizer(release(resource))

      } yield resource
    }

  val run =
    for {
      _ <- acquireRelease(Console.printLine("Acquired!"))(_ => Console.printLine("Released!").orDie)
      _ <- ZIO.fail("Uh oh!")
    } yield ()
}

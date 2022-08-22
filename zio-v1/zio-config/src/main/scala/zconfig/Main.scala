package zconfig

object Main {
    /**
    * database host
    * a port
    * username
    * password
    */
    def main(args: Array[String]): Unit = {
        val systemEnv: Map[String, String] = sys.systemEnv

        /** Version 1:
        val databaseHost = systemEnv("DATABASE_HOST")
        val port = systemEnv("PORT")
        val username = systemEnv("USERNAME")
        val password = systemEnv("PASSWORD")

        Core.run(ApplicationConfig(
            database,
            port.toInt,
            username,
            password
        ))
        **/

        /** Version 2: Option[String] to handle absence of value
        val databaseHost: Option[String] = systemEnv.get("DATABASE_HOST")
        val port: Option[String] = systemEnv.get("PORT")
        val username: Option[String] = systemEnv.get("USERNAME")
        val password: Option[String] = systemEnv.get("PASSWORD")

        for {
            db <- databaseHost
            pt <- port
            un <- username
            pw <- password
        } yield Core.run(ApplicationConfig(
            database,
            port.toInt,
            username,
            password
        ))
        **/

        /** Version 3: Either[String, String] to get values
        val databaseHost: Either[String, String] = systemEnv.get("DATABASE_HOST").toRight("DATABASE_HOST does not exist in sysenv")
        val port: Either[String, String] = systemEnv.get("PORT").toRight("PORT does not exist in sysenv")
        val username: Either[String, String] = systemEnv.get("USERNAME").toRight("USERNAME does not exist in sysenv")
        val password: Either[String, String] = systemEnv.get("PASSWORD").toRight("PASSWORD does not exist in sysenv")

        val result: Either[String, Unit] =
            for {
                db <- databaseHost
                pt <- port
                un <- username
                pw <- password
            } yield Core.run(ApplicationConfig(
                database,
                port.toInt,
                username,
                password
            ))
        
        print(result);
        // We need something like: database with port with username with password.
        **/
    }
}

object Core {
    def run(applicationConfig: ApplicationConfig): Unit = {
        println(s"Running the application using application config ${applicationConfig}")
    }
    // Version 1: Error - NoSuchElementException: DATABASE_HOST
    // Version 2: Application does nothing
    // Version 3: Left(DATABASE_HOST does not exist in system env), It is impossible accumulate the errors
    //  b/c in the for comprehension they are chained 
}

final case class ApplicationConfig(
    host: String,
    port: Int,
    username: String,
    password: String
)
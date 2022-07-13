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
    }
}

object Core {
    def run(applicationConfig: ApplicationConfig): Unit = {
        println(s"Running the application using application config ${applicationConfig}")
    }
}

final case class ApplicationConfig(
    host: String,
    port: Int,
    username: String,
    password: String
)
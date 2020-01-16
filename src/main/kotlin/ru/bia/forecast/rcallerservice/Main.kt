package ru.bia.forecast.rcallerservice

import java.io.IOException
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory


fun main(args: Array<String>) {
    val options = Options()
    options.addOption("p", "port", true, "TCP port")
    val parser = DefaultParser();
    val commandLine = parser.parse(options, args);

    App(commandLine).run()
}

class App(val commandLine: CommandLine) {
    fun run() {
        try {
            val port = commandLine.getOptionValue("port", "8080")
            val BASE_URI = URI.create("http://0.0.0.0:${port}/")

            val applicationConfig = ApplicationConfig()
            val server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, applicationConfig, false)
            Runtime.getRuntime().addShutdownHook(Thread(Runnable { server.shutdownNow() }))
            server.start()

            println(String.format("Application started.%nStop the application using CTRL+C"))

            Thread.currentThread().join()
        } catch (ex: IOException) {
            Logger.getLogger(App::class.java.name).log(Level.SEVERE, null, ex)
        } catch (ex: InterruptedException) {
            Logger.getLogger(App::class.java.name).log(Level.SEVERE, null, ex)
        }
    }

}

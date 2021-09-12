package com.github.kopilov.rcallerservice

import java.io.IOException
import java.net.URI
import java.util.logging.Level
import java.util.logging.Logger
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.CommandLine
import org.apache.commons.cli.Options
import org.apache.commons.pool2.impl.GenericObjectPool
import org.glassfish.hk2.utilities.binding.AbstractBinder
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory
import com.github.kopilov.rcallerpool.RCallerContainer
import com.github.kopilov.rcallerpool.RDependencies
import com.github.kopilov.rcallerpool.createRCallerPool


fun main(args: Array<String>) {
    val options = Options()
    options.addOption("p", "port", true, "TCP port")
    options.addOption("exp", "expiration_time", true, "RCaller session expiration time (how long passive session will be kept in the pool) in seconds")
    options.addOption("lib", "enable_library", true, "CRAN library to be required before running client requests. Define more than one if necessary.")
    options.addOption("src", "add_script", true, "Any R source file to be loaded in REPL before running client requests. Define more than one if necessary.")
    val parser = DefaultParser();
    val commandLine = parser.parse(options, args);

    App(commandLine).run()
}

fun configurePool(commandLine: CommandLine): GenericObjectPool<RCallerContainer> {
    val expirationTime = commandLine.getOptionValue("expiration_time", "60").toInt()
    val libraries = commandLine.getOptionValues("enable_library")
    val librariesList = libraries?.asList() ?: emptyList()
    val scripts = commandLine.getOptionValues("add_script")
    val scriptsList = scripts?.asList() ?: emptyList()
    val dependencies = RDependencies(libraries = librariesList, scripts = scriptsList, resources = emptyList())
    return createRCallerPool(expirationTime, dependencies)
}

class App(private val commandLine: CommandLine) {
    companion object {
        var rCallerPool: GenericObjectPool<RCallerContainer>? = null;
    }

    fun run() {
        try {
            val port = commandLine.getOptionValue("port", "8080")
            val BASE_URI = URI.create("http://0.0.0.0:${port}/")
            rCallerPool = configurePool(commandLine)

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

package ru.bia.forecast.rcallerservice

import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.glassfish.jersey.server.ContainerRequest
import javax.ws.rs.*
import kotlin.system.exitProcess

fun createRCallerPool(expirationTime: Int): GenericObjectPool<RCallerContainer> {
    val poolConfig = GenericObjectPoolConfig<RCallerContainer>()
    poolConfig.setTimeBetweenEvictionRunsMillis(expirationTime * 1000L);
    poolConfig.setMinEvictableIdleTimeMillis(expirationTime * 1000L);
    return GenericObjectPool<RCallerContainer>(RCallerFactory(), poolConfig);
}

@Path("")
class RCallerService {

    companion object {
        val expirationTime = 60 //Time in seconds from last resource usage to it's removing
        val rCallerPool = createRCallerPool(expirationTime)
    }

    /**
     * Calculate R script with double array output
     */
    @Path("/double_array")
    @POST
    @Consumes("text/plain")
    @Produces("text/csv")
    fun calculateDoubleArray(script: String, @QueryParam("result") resultNameParam: String?): String {
        var rCallerContainer: RCallerContainer? = null
        try {
            rCallerContainer = rCallerPool.borrowObject()
            val resultName = resultNameParam ?: "result"
            rCallerContainer.runAndReturnResultOnline(script, resultName)
            val doubleArrayResult: DoubleArray = rCallerContainer.getDoubleArrayResult(resultName)!!
            val stringArrayResult = doubleArrayResult.map { d: Double -> "$d" }
            return java.lang.String.join(";", stringArrayResult) + "\n"
        } finally {
            if (rCallerContainer != null) {
                rCallerPool.returnObject(rCallerContainer)
            }
        }
    }

    /**
     * Kubernetes readiness probe
     */
    @Path("/ready")
    @GET
    fun readinessProbe(): String {
        return "Server is ready\n";
    }

    /**
     * Kubernetes liveness probe
     */
    @Path("/alive")
    @GET
    fun livenessProbe(): String {
        return "Server is alive\n";
    }

    /**
     * Gracefully shutdown for complete jobs
     */
    @Path("/shutdown")
    @DELETE
    fun terminate(request: ContainerRequest): String {
        println(request.absolutePath.host)
        val message = "RCallerService is shutting down!"
        return if (request.absolutePath.host == "127.0.0.1") {
            Thread { ->
                println(message)
                Thread.sleep(1000)
                exitProcess( 0)
            }.start()
            message
        } else {
            ""
        }
    }
}

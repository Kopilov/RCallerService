package com.github.kopilov.rcallerservice

import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.glassfish.jersey.server.ContainerRequest
import java.util.*
import javax.ws.rs.*
import javax.ws.rs.core.Response
import kotlin.system.exitProcess

fun createRCallerPool(expirationTime: Int): GenericObjectPool<RCallerContainer> {
    val poolConfig = GenericObjectPoolConfig<RCallerContainer>()
    poolConfig.timeBetweenEvictionRunsMillis = expirationTime * 1000L;
    poolConfig.minEvictableIdleTimeMillis = expirationTime * 1000L;
    poolConfig.testOnBorrow = true
    poolConfig.testOnCreate = true
    poolConfig.testOnReturn = true
    return GenericObjectPool(RCallerFactory(), poolConfig);
}

@Path("")
class RCallerService {

    companion object {
        val expirationTime = 60 //Time in seconds from last resource usage to it's removing
        val rCallerPool = createRCallerPool(expirationTime)
    }

    /**
     * Calculate R script with double array output written in [resultNameParam] variable.
     * If [timeout] is not null, wait not more than this period (in seconds). Return 504 HTTP status if expired.
     */
    @Path("/double_array")
    @POST
    @Consumes("application/x-www-form-urlencoded", "text/plain")
    @Produces("text/csv")
    fun calculateDoubleArray(
        script: String,
        @QueryParam("result") resultNameParam: String?,
        @QueryParam("timeout") timeout: Int?
    ): Response {
        var rCallerContainer: RCallerContainer? = null
        try {
            rCallerContainer = rCallerPool.borrowObject()
            val resultName = resultNameParam ?: "result"
            val resultReady = rCallerContainer.runAndReturnResultOnline(script, resultName, timeout)
            return if (resultReady) {
                val doubleArrayResult: DoubleArray = rCallerContainer.getDoubleArrayResult(resultName)!!
                val stringArrayResult = doubleArrayResult.map { d: Double -> "$d" }
                Response.ok().entity(java.lang.String.join(";", stringArrayResult) + "\n").build()
            } else {
                Response.status(Response.Status.GATEWAY_TIMEOUT).entity("Timeout $timeout seconds expired").build()
            }
        } catch (e: Throwable) {
            e.printStackTrace();
            return Response.status(Response.Status.fromStatusCode(500)).entity(e.message).build()
        } finally {
            if (rCallerContainer != null) {
                rCallerPool.returnObject(rCallerContainer)
            }
        }
    }

    /**
     * Calculate R script with text array output written in [resultNameParam] variable.
     * If [timeout] is not null, wait not more than this period (in seconds). Return 504 HTTP status if expired.
     */
    @Path("/text_array")
    @POST
    @Consumes("application/x-www-form-urlencoded", "text/plain")
    @Produces("text/csv")
    fun calculateTextArray(
        script: String,
        @QueryParam("result") resultNameParam: String?,
        @QueryParam("timeout") timeout: Int?
    ): Response {
        var rCallerContainer: RCallerContainer? = null
        try {
            rCallerContainer = rCallerPool.borrowObject()
            val resultName = resultNameParam ?: "result"
            val resultReady = rCallerContainer.runAndReturnResultOnline(script, resultName, timeout)
            return if (resultReady) {
                val stringArrayResult = rCallerContainer.getStringArrayResult(resultName)
                val result = StringJoiner(";")
                stringArrayResult?.map { cell ->
                    val cellRepl = if (cell.contains(";")) {
                        "\"" + cell.replace("\"", "\"\"") + "\""
                    } else {
                        cell
                    }
                    result.add(cellRepl)
                }
                Response.ok().entity(result.toString()).build()
            } else {
                Response.status(Response.Status.GATEWAY_TIMEOUT).entity("Timeout $timeout seconds expired").build()
            }
        } catch (e: Throwable) {
            e.printStackTrace();
            return Response.status(Response.Status.fromStatusCode(500)).entity(e.message).build()
        } finally {
            if (rCallerContainer != null) {
                rCallerPool.returnObject(rCallerContainer)
            }
        }
    }

    /**
     * Calculate R script with double array output written in [resultNameParam] variable.
     * If [timeout] is not null, wait not more than this period (in seconds). Return 504 HTTP status if expired.
     */
    @Path("/text")
    @POST
    @Consumes("application/x-www-form-urlencoded", "text/plain")
    @Produces("text/plain")
    fun calculatePlainText(
        script: String,
        @QueryParam("result") resultNameParam: String?,
        @QueryParam("timeout") timeout: Int?
    ): Response {
        var rCallerContainer: RCallerContainer? = null
        try {
            rCallerContainer = rCallerPool.borrowObject()
            val resultName = resultNameParam ?: "result"
            val resultReady = rCallerContainer.runAndReturnResultOnline(script, resultName, timeout)
            return if (resultReady) {
                val stringResult = rCallerContainer.getStringResult(resultName)
                Response.ok().entity(stringResult).build()
            } else {
                Response.status(Response.Status.GATEWAY_TIMEOUT).entity("Timeout $timeout seconds expired").build()
            }
        } catch (e: Throwable) {
            e.printStackTrace();
            return Response.status(Response.Status.fromStatusCode(500)).entity(e.message).build()
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

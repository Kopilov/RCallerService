package com.github.kopilov.rcallerservice

import java.util.StringJoiner
import jakarta.ws.rs.Path
import jakarta.ws.rs.POST
import jakarta.ws.rs.GET
import jakarta.ws.rs.DELETE
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.Response
import kotlin.system.exitProcess
import org.glassfish.jersey.server.ContainerRequest
import org.apache.commons.pool2.impl.GenericObjectPool
import com.github.kopilov.rcallerpool.RCallerContainer

@Path("")
class RCallerService {

    val rCallerPool: GenericObjectPool<RCallerContainer> by lazy {
        App.rCallerPool!!
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
        @QueryParam("timeout") timeout: Int?,
        @QueryParam("catch") catch: Boolean?,
        @QueryParam("by_file") byFile: Boolean?
    ): Response {
        var rCallerContainer: RCallerContainer? = null
        try {
            rCallerContainer = rCallerPool.borrowObject()
            val resultName = resultNameParam ?: "result"
            val resultReady = if (byFile == true) {
                rCallerContainer.runAndReturnResult(script, resultName, timeout, catch ?: false)
            } else {
                rCallerContainer.runAndReturnResultOnline(script, resultName, timeout, catch ?: false)
            }
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
        @QueryParam("timeout") timeout: Int?,
        @QueryParam("catch") catch: Boolean?,
        @QueryParam("by_file") byFile: Boolean?
    ): Response {
        var rCallerContainer: RCallerContainer? = null
        try {
            rCallerContainer = rCallerPool.borrowObject()
            val resultName = resultNameParam ?: "result"
            val resultReady = if (byFile == true) {
                rCallerContainer.runAndReturnResult(script, resultName, timeout, catch ?: false)
            } else {
                rCallerContainer.runAndReturnResultOnline(script, resultName, timeout, catch ?: false)
            }
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
        @QueryParam("timeout") timeout: Int?,
        @QueryParam("catch") catch: Boolean?,
        @QueryParam("by_file") byFile: Boolean?
    ): Response {
        var rCallerContainer: RCallerContainer? = null
        try {
            rCallerContainer = rCallerPool.borrowObject()
            val resultName = resultNameParam ?: "result"
            val resultReady = if (byFile == true) {
                rCallerContainer.runAndReturnResult(script, resultName, timeout, catch ?: false)
            } else {
                rCallerContainer.runAndReturnResultOnline(script, resultName, timeout, catch ?: false)
            }
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

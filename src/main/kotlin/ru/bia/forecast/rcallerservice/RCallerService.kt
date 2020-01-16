package ru.bia.forecast.rcallerservice

import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import javax.ws.rs.*

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
}

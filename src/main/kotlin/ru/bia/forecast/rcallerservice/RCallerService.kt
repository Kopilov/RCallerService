package ru.bia.forecast.rcallerservice

import javax.ws.rs.*

@Path("")
class RCallerService {

    /**
     * Calculate R script with double array output
     */
    @Path("/double_array")
    @POST
    @Consumes("text/plain")
    @Produces("text/csv")
    fun calculateDoubleArray(script: String, @QueryParam("result") resultNameParam: String?): String {
        val resultName = resultNameParam ?: "result"
        val rCallerContainer = RCallerContainer()
        rCallerContainer.runAndReturnResultOnline(script, resultName)
        val doubleArrayResult: DoubleArray = rCallerContainer.getDoubleArrayResult(resultName)!!
        val stringArrayResult = doubleArrayResult.map { d: Double -> "$d" }
        return java.lang.String.join(";", stringArrayResult) + "\n"
    }

}

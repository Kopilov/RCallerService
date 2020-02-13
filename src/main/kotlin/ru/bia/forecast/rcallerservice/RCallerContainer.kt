package ru.bia.forecast.rcallerservice

import com.github.rcaller.rstuff.RCaller
import com.github.rcaller.rstuff.RCode
import java.lang.System.currentTimeMillis

class RCallerContainer() {

    private val rcaller = RCaller.create()
    private val rcode = RCode.create()
    private val template = rcode.toString();
    private var hasZombieCalculation = false

    init {
        rcaller.rCode = rcode
    }

    fun obtain() {
        rcode.addRCode("rm(list=ls())")
        rcode.addRCode(template)
        rcode.addRCode("library(\"forecast\")")
        rcode.addRCode("library(\"forecTheta\")")
    }

    fun release() {
        rcode.clear()
        rcaller.deleteTempFiles()
    }

    fun hasNoZombieCalculation(): Boolean {
        return !hasZombieCalculation
    }

    fun runAndReturnResultOnline(source: String, resultName: String, timeout: Int?): Boolean {
        rcode.addRCode(source)
        if (timeout is Int) {
            //start calculation in separate thread
            val rCallerCalculation = Thread ({rcaller.runAndReturnResultOnline(resultName)})
            rCallerCalculation.start()
            val startedAt = currentTimeMillis();
            //sleep while timeout not expired and calculation actually performs
            while ((currentTimeMillis() - startedAt) / 1000 < timeout && rCallerCalculation.isAlive) {
                Thread.sleep(1)
            }
            if (rCallerCalculation.isAlive) {
                //calculation still performs => timeout expired
                //Invalidate this RCallerContainer
                hasZombieCalculation = true
                //try to kill process after return
                Thread ({close()}).start()
                return false
            } else {
                //OK
                return true
            }
        } else {
            rcaller.runAndReturnResultOnline(resultName)
            return true
        }
    }

    fun getDoubleArrayResult(resultName: String): DoubleArray? {
        return rcaller.parser.getAsDoubleArray(resultName)
    }

    fun close() {
        rcaller.stopStreamConsumers()
        rcaller.StopRCallerOnline()
    }
}

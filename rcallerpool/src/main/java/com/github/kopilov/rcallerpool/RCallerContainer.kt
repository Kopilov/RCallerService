package com.github.kopilov.rcallerpool

import com.github.rcaller.rstuff.RCaller
import com.github.rcaller.rstuff.RCode
import java.lang.System.currentTimeMillis
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class RCallerContainer(private val globalDependencies: RDependencies) {

    constructor() : this(RDependencies())

    private val rcaller = RCaller.create()
    private val rcode = RCode.create()
    private val template = rcode.toString();
    private val hasZombieCalculation = AtomicBoolean(false)

    init {
        rcaller.rCode = rcode
    }

    fun obtain() {
        rcode.addRCode("rm(list=ls())")
        rcode.addRCode(template)
        rcode.addRCode(globalDependencies.generateLoadingScript())
    }

    fun release() {
        rcode.clear()
        rcaller.deleteTempFiles()
    }

    fun hasNoZombieCalculation(): Boolean {
        return !hasZombieCalculation.get()
    }

    fun runAndReturnResultOnline(source: String, resultName: String, timeout: Int?): Boolean {
        return runAndReturnResultOnline(source, resultName, RDependencies(), timeout)
    }

    fun runAndReturnResultOnline(source: String, resultName: String, dependencies: RDependencies, timeout: Int?): Boolean {
        rcode.addRCode(dependencies.generateLoadingScript())
        rcode.addRCode(source)
        if (timeout is Int) {
            //start calculation in separate thread
            val rCallerCalculation = Thread {rcaller.runAndReturnResultOnline(resultName)}
            rCallerCalculation.start()
            val startedAt = currentTimeMillis();
            //sleep while timeout not expired and calculation actually performs
            while ((currentTimeMillis() - startedAt) / 1000 < timeout && rCallerCalculation.isAlive) {
                Thread.sleep(1)
            }
            if (rCallerCalculation.isAlive) {
                //calculation still performs => timeout expired
                //Invalidate this RCallerContainer
                hasZombieCalculation.set(true)
                //Kill process after return
                Thread ({rcaller.stopRCallerAsync()}).start()
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

    fun getDoubleMatrixResult(resultName: String): Array<out DoubleArray>? {
        return rcaller.parser.getAsDoubleMatrix(resultName)
    }

    fun getDoubleArrayResult(resultName: String): DoubleArray? {
        return rcaller.parser.getAsDoubleArray(resultName)
    }

    fun getStringArrayResult(resultName: String): Array<out String>? {
        return rcaller.parser.getAsStringArray(resultName)
    }

    fun getStringResult(resultName: String): String? {
        val joiner = StringJoiner("")
        getStringArrayResult(resultName)?.map { element -> joiner.add(element) }
        return joiner.toString()
    }

    fun close() {
        if (hasNoZombieCalculation()) {
            rcaller.stopStreamConsumers()
            rcaller.stopRCallerOnline()
        }
        rcaller.deleteTempFiles()
    }
}

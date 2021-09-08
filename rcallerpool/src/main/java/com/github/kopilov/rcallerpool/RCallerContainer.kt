package com.github.kopilov.rcallerpool

import com.github.rcaller.rstuff.FailurePolicy
import com.github.rcaller.rstuff.RCaller
import com.github.rcaller.rstuff.RCallerOptions
import java.lang.System.currentTimeMillis
import java.util.Date
import java.util.StringJoiner
import java.util.concurrent.atomic.AtomicBoolean

class RCallerContainer {

    private fun createRCallerOptions(): RCallerOptions? {
        val rCallerOptions = RCallerOptions.create()
        rCallerOptions.failurePolicy = FailurePolicy.CONTINUE
        rCallerOptions.setFailIfArrowNotAvailable(true)
        return rCallerOptions
    }

    private val rcaller = RCaller.create(createRCallerOptions())
    private val rcode = rcaller.rCode
    private val hasZombieCalculation = AtomicBoolean(false)
    private val hasFailedCalculation = AtomicBoolean(false)

    private val systemDependenciesListName: String?

    constructor(): this(RDependencies()) {}

    constructor(dependencies: RDependencies) {
        //Run plug code snippet to init the RCaller, send toplevel dependencies to REPL and read list of system objects
        //that should not be removed on pooling (second reading adds a new variable itself :-) )
        systemDependenciesListName = "system_dependencies_${Date().time}"
        val startScript = """
            ${dependencies.generateLoadingScript()}
            $systemDependenciesListName <- ls()
            $systemDependenciesListName <- ls()
            """.trimIndent()
        runAndReturnResultOnline(startScript, systemDependenciesListName, null)
        getStringArrayResult(systemDependenciesListName)
        rcode.clearOnline()
    }

    fun obtain() {
        rcode.addRCode("rm(list = setdiff(ls(), $systemDependenciesListName))")
    }

    fun release() {
        rcode.clearOnline()
        rcaller.deleteTempFiles()
    }

    fun isValid(): Boolean {
        return !hasZombieCalculation.get() && !hasFailedCalculation.get()
    }

    fun runAndReturnResultOnline(source: String, resultName: String, timeout: Int?, addTryCatch: Boolean = false): Boolean {
        return runAndReturnResultOnline(source, resultName, RDependencies(), timeout, addTryCatch)
    }

    fun runAndReturnResultOnline(source: String, resultName: String, dependencies: RDependencies, timeout: Int?, addTryCatch: Boolean = false): Boolean {
        rcode.addRCode(dependencies.generateLoadingScript())
        rcode.addRCode(source)
        val tryRunRCaller = {
            try {
                rcaller.runAndReturnResultOnline(resultName, addTryCatch)
            } catch (e: Exception) {
                hasFailedCalculation.set(true)
                throw e
            }
        }
        if (timeout is Int) {
            //start calculation in separate thread
            val rCallerCalculation = Thread(tryRunRCaller)
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
                return !hasFailedCalculation.get()
            }
        } else {
            tryRunRCaller()
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
        if (isValid()) {
            rcaller.stopStreamConsumers()
            rcaller.stopRCallerOnline()
        }
        rcaller.deleteTempFiles()
    }
}

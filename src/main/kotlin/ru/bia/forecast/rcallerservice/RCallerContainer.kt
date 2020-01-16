package ru.bia.forecast.rcallerservice

import com.github.rcaller.rstuff.RCaller
import com.github.rcaller.rstuff.RCode

class RCallerContainer {

    val rcaller = RCaller.create()
    val rcode = RCode.create()
    val template = rcode.toString();

    constructor () {
        rcaller.rCode = rcode;
    }

    fun obtain() {
        rcode.addRCode("rm(list=ls())")
        rcode.addRCode(template)
        rcode.addRCode("library(\"forecast\")")
        rcode.addRCode("library(\"forecTheta\")")
    }

    fun release() {
        rcode.clear();
        rcaller.deleteTempFiles();
    }

    fun runAndReturnResultOnline(source: String, resultName: String) {
        rcode.addRCode(source);
        rcaller.runAndReturnResultOnline(resultName);
    }

    fun getDoubleArrayResult(resultName: String): DoubleArray? {
        return rcaller.parser.getAsDoubleArray(resultName);
    }

    fun close() {
        rcaller.stopStreamConsumers()
        rcaller.StopRCallerOnline()
    }
}

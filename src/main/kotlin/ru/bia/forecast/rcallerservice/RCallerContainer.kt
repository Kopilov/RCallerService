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
    //    super.obtain()
        rcode.addRCode("rm(list=ls())")
        rcode.addRCode(template)
        rcode.addRCode("library(\"forecast\")")
        rcode.addRCode("library(\"forecTheta\")")
    }

    fun release(e: Throwable?) {
//        if (exists err = e) {
//            err.printStackTrace();
//        }
        rcode.clear();
        rcaller.deleteTempFiles();
//        super.release(e);
    }

    fun runAndReturnResultOnline(source: String, resultName: String) {
        obtain()
        rcode.addRCode(source);
        rcaller.runAndReturnResultOnline(resultName);
        release(null)
    }

    fun getDoubleArrayResult(resultName: String): DoubleArray? {
        return rcaller.parser.getAsDoubleArray(resultName);
    }

}

package com.github.kopilov.rcallerpool

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder

/**
 * Any dependencies (libraries, packages, scripts) for running some specified R code in user space by RCaller
 */
class RDependencies(
    /**
     * List of required libraries that are installed in R runtime context and can be loaded with 'require' R command
     */
    private val libraries: List<String>,
    /**
     * List of required scripts that are placed anywhere in system and can be loaded with 'source' R command
     */
    private val scripts: List<String>,
    /**
     * List of resources that are placed in Java classpath, can be loaded with ClassLoader.getSystemResources and transferred to R stdin
     */
    private val resources: List<String>
) {
    constructor() : this(emptyList<String>(), emptyList<String>(), emptyList<String>())

    fun generateLoadingScript(): String {
        val loadingScript = StringBuilder()
        libraries.map { loadingScript.appendLine("require(\"$it\")") }
        loadingScript.appendLine()
        resources.map {
            val resourceURL = ClassLoader.getSystemResources(it).toList().firstOrNull()
            if (resourceURL == null) {
                throw Exception("Error loading R dependency resource $it")
            }
            loadingScript.appendLine()
            loadingScript.appendLine("#========== Begin loading resource $it ==========")
            resourceURL.openStream().use { stream ->
                BufferedReader(InputStreamReader(stream)).lines().forEach { line -> loadingScript.appendLine(line) }
            }
            loadingScript.appendLine("#========== End loading resource $it ==========")
            loadingScript.appendLine()
        }

        scripts.map { loadingScript.appendLine("source(\"$it\")") }
        loadingScript.appendLine()

        return loadingScript.toString()
    }

}

package com.github.kopilov.rcallerpool

import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.PooledObjectFactory
import org.apache.commons.pool2.impl.DefaultPooledObject
import java.util.prefs.Preferences

/**
 * Use this class to init new pool (manually or by [createRCallerPool] method).
 * All [dependencies] elements would be loaded to R REPL on start (only once)
 */
class RCallerFactory(val dependencies: RDependencies): PooledObjectFactory<RCallerContainer> {

    constructor(): this(RDependencies())

    override fun makeObject(): PooledObject<RCallerContainer> {
        return DefaultPooledObject(RCallerContainer(dependencies))
    }

    override fun destroyObject(p: PooledObject<RCallerContainer>) {
        p.`object`.close()
    }

    override fun validateObject(p: PooledObject<RCallerContainer>?): Boolean {
        return p != null && p.`object` != null && p.`object`.isValid()
    }

    override fun activateObject(p: PooledObject<RCallerContainer>) {
        p.`object`.obtain()
    }

    override fun passivateObject(p: PooledObject<RCallerContainer>) {
        p.`object`.release()
    }
}

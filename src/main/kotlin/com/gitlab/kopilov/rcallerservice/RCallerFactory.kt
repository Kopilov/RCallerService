package com.gitlab.kopilov.rcallerservice

import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.PooledObjectFactory
import org.apache.commons.pool2.impl.DefaultPooledObject

class RCallerFactory: PooledObjectFactory<RCallerContainer> {
    override fun makeObject(): PooledObject<RCallerContainer> {
        return DefaultPooledObject(RCallerContainer())
    }

    override fun destroyObject(p: PooledObject<RCallerContainer>) {
        p.`object`.close()
    }

    override fun validateObject(p: PooledObject<RCallerContainer>?): Boolean {
        return p != null && p.`object` != null && p.`object`.hasNoZombieCalculation()
    }

    override fun activateObject(p: PooledObject<RCallerContainer>) {
        p.`object`.obtain()
    }

    override fun passivateObject(p: PooledObject<RCallerContainer>) {
        p.`object`.release()
    }
}
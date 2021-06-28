package com.github.kopilov.rcallerarrow

import com.github.kopilov.rcallerpool.RCallerContainer
import com.github.kopilov.rcallerpool.RCallerFactory
import com.github.rcaller.rstuff.RCaller
import org.apache.arrow.memory.BufferAllocator
import org.apache.commons.pool2.PooledObject
import org.apache.commons.pool2.PooledObjectFactory
import org.apache.commons.pool2.impl.DefaultPooledObject
import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import javax.xml.stream.util.XMLEventAllocator

class RCallerArrowFactory(val allocator: BufferAllocator): RCallerFactory() {
    override fun createRCaller(): RCaller {
        return RCallerArrowPipe.create(allocator)
    }
}

fun createRCallerArrowPool(allocator: BufferAllocator, expirationTime: Int): GenericObjectPool<RCallerContainer> {
    val poolConfig = GenericObjectPoolConfig<RCallerContainer>()
    poolConfig.timeBetweenEvictionRunsMillis = expirationTime * 1000L
    poolConfig.minEvictableIdleTimeMillis = expirationTime * 1000L
    poolConfig.testOnBorrow = true
    poolConfig.testOnCreate = true
    poolConfig.testOnReturn = true
    poolConfig.maxIdle = Runtime.getRuntime().availableProcessors()
    poolConfig.maxTotal = Runtime.getRuntime().availableProcessors() * 2
    return GenericObjectPool(RCallerArrowFactory(allocator), poolConfig);
}

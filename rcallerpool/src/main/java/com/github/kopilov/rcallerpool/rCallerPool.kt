package com.github.kopilov.rcallerpool

import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import java.time.Duration

fun createRCallerPool(expirationTime: Long, dependencies: RDependencies): GenericObjectPool<RCallerContainer> {
    val poolConfig = GenericObjectPoolConfig<RCallerContainer>()
    poolConfig.timeBetweenEvictionRuns = Duration.ofSeconds(expirationTime)
    poolConfig.minEvictableIdleTime = Duration.ofSeconds(expirationTime)
    poolConfig.testOnBorrow = true
    poolConfig.testOnCreate = true
    poolConfig.testOnReturn = true
    poolConfig.maxIdle = Runtime.getRuntime().availableProcessors()
    poolConfig.maxTotal = Runtime.getRuntime().availableProcessors() * 2
    return GenericObjectPool(RCallerFactory(dependencies), poolConfig)
}

fun createRCallerPool(expirationTime: Int, dependencies: RDependencies): GenericObjectPool<RCallerContainer> {
    return createRCallerPool(expirationTime.toLong(), dependencies)
}

fun createRCallerPool(expirationTime: Long): GenericObjectPool<RCallerContainer> {
    return createRCallerPool(expirationTime, RDependencies())
}

fun createRCallerPool(expirationTime: Int): GenericObjectPool<RCallerContainer> {
    return createRCallerPool(expirationTime.toLong(), RDependencies())
}


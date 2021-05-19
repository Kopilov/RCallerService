package com.github.kopilov.rcallerpool

import org.apache.commons.pool2.impl.GenericObjectPool
import org.apache.commons.pool2.impl.GenericObjectPoolConfig

fun createRCallerPool(expirationTime: Int): GenericObjectPool<RCallerContainer> {
    val poolConfig = GenericObjectPoolConfig<RCallerContainer>()
    poolConfig.timeBetweenEvictionRunsMillis = expirationTime * 1000L
    poolConfig.minEvictableIdleTimeMillis = expirationTime * 1000L
    poolConfig.testOnBorrow = true
    poolConfig.testOnCreate = true
    poolConfig.testOnReturn = true
    poolConfig.maxIdle = Runtime.getRuntime().availableProcessors()
    poolConfig.maxTotal = Runtime.getRuntime().availableProcessors() * 2
    return GenericObjectPool(RCallerFactory(), poolConfig);
}

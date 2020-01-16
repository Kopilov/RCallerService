package ru.bia.forecast.rcallerservice

import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig
import ru.bia.forecast.rcallerservice.RCallerService

/**
 * Jersey resources list loaded on Grizzly startup
 */
class ApplicationConfig : ResourceConfig(
        RCallerService::class.java,
        JacksonFeature::class.java
)

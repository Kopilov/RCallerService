package com.github.kopilov.rcallerservice

import org.glassfish.jersey.jackson.JacksonFeature
import org.glassfish.jersey.server.ResourceConfig

/**
 * Jersey resources list loaded on Grizzly startup
 */
class ApplicationConfig : ResourceConfig(
        RCallerService::class.java,
        JacksonFeature::class.java
)

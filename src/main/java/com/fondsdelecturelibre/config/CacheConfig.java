package com.fondsdelecturelibre.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Spring Boot will auto-configure SimpleCache based on application.properties
}

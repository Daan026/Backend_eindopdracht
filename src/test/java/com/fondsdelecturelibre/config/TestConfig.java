package com.fondsdelecturelibre.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.CommandLineRunner;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public CommandLineRunner testAdminInitializer() {
        return args -> {
        };
    }
}

package com.example.healthinfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot 4 Health Info application.
 * Bootstraps the Spring context with auto-configuration enabled.
 */
@SpringBootApplication
public class HealthInfoApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthInfoApplication.class, args);
    }
}

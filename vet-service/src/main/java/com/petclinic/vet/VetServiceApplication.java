package com.petclinic.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the standalone Vet microservice.
 * Bootstraps the Spring Boot application context.
 */
@SpringBootApplication
public class VetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetServiceApplication.class, args);
    }
}

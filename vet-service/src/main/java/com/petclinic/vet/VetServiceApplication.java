package com.petclinic.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the standalone Vet microservice.
 * JPA auditing is enabled in a separate config class to avoid @WebMvcTest issues.
 */
@SpringBootApplication
public class VetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetServiceApplication.class, args);
    }
}

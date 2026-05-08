package com.petclinic.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the standalone Vet microservice.
 * Extracted from the Spring PetClinic monolith to manage
 * veterinarians and their specialties independently.
 */
@SpringBootApplication
public class VetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetServiceApplication.class, args);
    }
}

package com.petclinic.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the standalone Vet microservice.
 * Extracted from the PetClinic monolith to manage veterinarians and specialties.
 */
@SpringBootApplication
public class VetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetServiceApplication.class, args);
    }
}

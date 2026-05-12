package com.petclinic.vet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Vet microservice.
 */
@SpringBootApplication
public class VetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VetServiceApplication.class, args);
    }
}

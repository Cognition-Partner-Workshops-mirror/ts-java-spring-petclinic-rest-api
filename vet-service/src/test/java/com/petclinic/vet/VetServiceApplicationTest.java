package com.petclinic.vet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifies the Spring Boot application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class VetServiceApplicationTest {

    @Test
    void contextLoads() {
        // Verifies the application context starts without errors
    }

    @Test
    void main_startsApplication() {
        // Covers the main method entry point
        VetServiceApplication.main(new String[]{"--spring.profiles.active=test"});
    }
}

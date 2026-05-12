package com.petclinic.vet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Smoke test verifying the application context loads successfully.
 */
@SpringBootTest
@ActiveProfiles("test")
class VetServiceApplicationTests {

    @Test
    void contextLoads() {
        // Verifies that all beans wire correctly and the app starts
    }
}

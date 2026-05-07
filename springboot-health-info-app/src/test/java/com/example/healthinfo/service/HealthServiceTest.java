package com.example.healthinfo.service;

import com.example.healthinfo.model.HealthStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for HealthService.
 * Validates that the health status is correctly returned.
 */
class HealthServiceTest {

    private HealthService healthService;

    /**
     * Set up a fresh HealthService instance before each test.
     */
    @BeforeEach
    void setUp() {
        healthService = new HealthService();
    }

    /**
     * Verify that getHealthStatus returns a non-null HealthStatus object.
     */
    @Test
    void getHealthStatus_shouldReturnNonNullStatus() {
        HealthStatus status = healthService.getHealthStatus();
        assertNotNull(status, "HealthStatus should not be null");
    }

    /**
     * Verify that the returned status value is "UP".
     */
    @Test
    void getHealthStatus_shouldReturnUpStatus() {
        HealthStatus status = healthService.getHealthStatus();
        assertEquals("UP", status.getStatus(), "Status should be UP");
    }

    /**
     * Verify that the description is populated with a meaningful message.
     */
    @Test
    void getHealthStatus_shouldReturnDescription() {
        HealthStatus status = healthService.getHealthStatus();
        assertEquals("Application is running and healthy", status.getDescription(),
            "Description should match expected message");
    }
}

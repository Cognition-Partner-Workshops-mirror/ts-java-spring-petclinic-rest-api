package com.example.healthinfo.controller;

import com.example.healthinfo.model.HealthStatus;
import com.example.healthinfo.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing the health check endpoint.
 * Delegates health status retrieval to HealthService.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    private final HealthService healthService;

    /**
     * Constructor injection of HealthService.
     *
     * @param healthService service providing health status information
     */
    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    /**
     * GET /api/health — Returns the current health status of the application.
     *
     * @return HealthStatus object serialized as JSON
     */
    @GetMapping("/health")
    public HealthStatus getHealth() {
        // Delegate to the health service to get the current status
        return healthService.getHealthStatus();
    }
}

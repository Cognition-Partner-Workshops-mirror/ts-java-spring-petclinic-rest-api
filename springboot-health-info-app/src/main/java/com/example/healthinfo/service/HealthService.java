package com.example.healthinfo.service;

import com.example.healthinfo.model.HealthStatus;
import org.springframework.stereotype.Service;

/**
 * Service responsible for determining the health status of the application.
 * Returns a HealthStatus object reflecting the current state of the system.
 */
@Service
public class HealthService {

    /**
     * Retrieves the current health status of the application.
     *
     * @return HealthStatus with status "UP" and a descriptive message
     */
    public HealthStatus getHealthStatus() {
        // Return a healthy status indicating the application is running normally
        return new HealthStatus("UP", "Application is running and healthy");
    }
}

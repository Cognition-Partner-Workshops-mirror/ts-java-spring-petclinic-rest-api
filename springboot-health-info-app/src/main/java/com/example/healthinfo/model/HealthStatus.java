package com.example.healthinfo.model;

/**
 * Model representing the health status of the application.
 * Contains the overall status and a description of the current state.
 */
public class HealthStatus {

    /** Overall health status (e.g., "UP", "DOWN") */
    private String status;

    /** Human-readable description of the health state */
    private String description;

    public HealthStatus() {
    }

    public HealthStatus(String status, String description) {
        this.status = status;
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

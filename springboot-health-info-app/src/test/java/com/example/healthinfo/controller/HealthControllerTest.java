package com.example.healthinfo.controller;

import com.example.healthinfo.model.HealthStatus;
import com.example.healthinfo.service.HealthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Web layer tests for HealthController.
 * Uses MockMvc to test the /api/health endpoint without starting a full server.
 * HealthService is mocked to isolate controller behavior.
 */
@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HealthService healthService;

    /**
     * Verify that GET /api/health returns HTTP 200 with the expected JSON body.
     */
    @Test
    void getHealth_shouldReturnHealthStatus() throws Exception {
        // Mock the service to return a known health status
        when(healthService.getHealthStatus())
            .thenReturn(new HealthStatus("UP", "Application is running and healthy"));

        // Perform GET request and validate the response
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.description").value("Application is running and healthy"));
    }

    /**
     * Verify that the endpoint correctly reflects a DOWN status from the service.
     */
    @Test
    void getHealth_shouldReturnDownStatusWhenServiceReportsDown() throws Exception {
        // Mock the service to return a DOWN status
        when(healthService.getHealthStatus())
            .thenReturn(new HealthStatus("DOWN", "Service is unavailable"));

        // Perform GET request and validate the DOWN response
        mockMvc.perform(get("/api/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("DOWN"))
            .andExpect(jsonPath("$.description").value("Service is unavailable"));
    }
}

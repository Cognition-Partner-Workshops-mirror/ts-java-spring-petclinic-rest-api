package com.example.healthinfo.controller;

import com.example.healthinfo.model.AppInfo;
import com.example.healthinfo.service.InfoService;
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
 * Web layer tests for InfoController.
 * Uses MockMvc to test the /api/info endpoint without starting a full server.
 * InfoService is mocked to isolate controller behavior.
 */
@WebMvcTest(InfoController.class)
class InfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InfoService infoService;

    /**
     * Verify that GET /api/info returns HTTP 200 with the expected JSON body.
     */
    @Test
    void getInfo_shouldReturnAppInfo() throws Exception {
        // Mock the service to return known application metadata
        when(infoService.getAppInfo())
            .thenReturn(new AppInfo("test-app", "1.0.0", "Test application"));

        // Perform GET request and validate the response
        mockMvc.perform(get("/api/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("test-app"))
            .andExpect(jsonPath("$.version").value("1.0.0"))
            .andExpect(jsonPath("$.description").value("Test application"));
    }

    /**
     * Verify that the endpoint correctly returns different metadata values.
     */
    @Test
    void getInfo_shouldReturnDifferentAppInfo() throws Exception {
        // Mock the service with different values to verify dynamic behavior
        when(infoService.getAppInfo())
            .thenReturn(new AppInfo("another-app", "2.0.0", "Another description"));

        // Perform GET request and validate the different response
        mockMvc.perform(get("/api/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("another-app"))
            .andExpect(jsonPath("$.version").value("2.0.0"))
            .andExpect(jsonPath("$.description").value("Another description"));
    }
}

package com.petclinic.vet.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.VetRequestDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VetIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void fullVetCrudLifecycle() throws Exception {
        // List seeded vets
        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(6))));

        // Create a new vet with specialties
        VetRequestDto createRequest = new VetRequestDto("Test", "Vetname", List.of(1, 2));
        String createJson = objectMapper.writeValueAsString(createRequest);

        String responseBody = mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("Vetname"))
            .andExpect(jsonPath("$.specialties", hasSize(2)))
            .andReturn().getResponse().getContentAsString();

        Integer newVetId = objectMapper.readTree(responseBody).get("id").asInt();

        // Get the created vet
        mockMvc.perform(get("/api/vets/" + newVetId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.specialties", hasSize(2)));

        // Update the vet
        VetRequestDto updateRequest = new VetRequestDto("Updated", "Name", List.of(1));
        mockMvc.perform(put("/api/vets/" + newVetId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.specialties", hasSize(1)));

        // Search by last name
        mockMvc.perform(get("/api/vets").param("lastName", "Name"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // Search by specialty ID
        mockMvc.perform(get("/api/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // Search by specialty name
        mockMvc.perform(get("/api/vets").param("specialtyName", "radiology"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // Delete the vet
        mockMvc.perform(delete("/api/vets/" + newVetId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Updated"));

        // Confirm deleted
        mockMvc.perform(get("/api/vets/" + newVetId))
            .andExpect(status().isNotFound());
    }

    @Test
    void createVet_validationError() throws Exception {
        VetRequestDto invalid = new VetRequestDto("", "", null);
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Bad Request"))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void createVet_invalidSpecialty() throws Exception {
        VetRequestDto request = new VetRequestDto("Test", "Vet", List.of(9999));
        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }
}

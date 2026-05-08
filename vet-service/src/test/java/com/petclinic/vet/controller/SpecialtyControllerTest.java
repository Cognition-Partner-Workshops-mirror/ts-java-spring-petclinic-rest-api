package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SpecialtyController using standalone MockMvc.
 * Tests HTTP request/response handling, validation, and error responses.
 */
@ExtendWith(MockitoExtension.class)
class SpecialtyControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SpecialtyService specialtyService;

    @InjectMocks
    private SpecialtyController specialtyController;

    private SpecialtyResponseDto sampleResponse;
    private SpecialtyRequestDto sampleRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(specialtyController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        sampleResponse = new SpecialtyResponseDto(1, "radiology");
        sampleRequest = new SpecialtyRequestDto("radiology");
    }

    @Test
    void listSpecialties_returnsAllSpecialties() throws Exception {
        when(specialtyService.getAllSpecialties()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void listSpecialties_returnsEmptyList() throws Exception {
        when(specialtyService.getAllSpecialties()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getSpecialty_returnsSpecialtyById() throws Exception {
        when(specialtyService.getSpecialtyById(1)).thenReturn(sampleResponse);

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_returns404WhenNotFound() throws Exception {
        when(specialtyService.getSpecialtyById(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void addSpecialty_createsNewSpecialty() throws Exception {
        when(specialtyService.createSpecialty(any(SpecialtyRequestDto.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void addSpecialty_returns400WhenNameBlank() throws Exception {
        SpecialtyRequestDto invalid = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation Error"))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void addSpecialty_returns400WhenNameTooLong() throws Exception {
        String longName = "a".repeat(81);
        SpecialtyRequestDto invalid = new SpecialtyRequestDto(longName);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    void updateSpecialty_updatesExistingSpecialty() throws Exception {
        SpecialtyResponseDto updated = new SpecialtyResponseDto(1, "surgery");
        SpecialtyRequestDto updateRequest = new SpecialtyRequestDto("surgery");
        when(specialtyService.updateSpecialty(eq(1), any(SpecialtyRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("surgery"));
    }

    @Test
    void updateSpecialty_returns404WhenNotFound() throws Exception {
        SpecialtyRequestDto updateRequest = new SpecialtyRequestDto("surgery");
        when(specialtyService.updateSpecialty(eq(99), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_deletesAndReturnsSpecialty() throws Exception {
        when(specialtyService.deleteSpecialty(1)).thenReturn(sampleResponse);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteSpecialty_returns404WhenNotFound() throws Exception {
        when(specialtyService.deleteSpecialty(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/specialties/99"))
            .andExpect(status().isNotFound());
    }
}

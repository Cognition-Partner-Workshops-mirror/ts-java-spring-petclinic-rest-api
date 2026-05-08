package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.dto.VetResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
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
 * Integration tests for VetController using standalone MockMvc.
 * Tests HTTP request/response handling, validation, and error responses.
 */
@ExtendWith(MockitoExtension.class)
class VetControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private VetService vetService;

    @InjectMocks
    private VetController vetController;

    private VetResponseDto sampleVetResponse;
    private VetRequestDto sampleVetRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vetController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

        SpecialtyResponseDto specialtyDto = new SpecialtyResponseDto(1, "radiology");
        sampleVetResponse = new VetResponseDto(1, "James", "Carter", List.of(specialtyDto));
        sampleVetRequest = new VetRequestDto("James", "Carter", List.of(specialtyDto));
    }

    @Test
    void listVets_returnsAllVets() throws Exception {
        when(vetService.getAllVets()).thenReturn(List.of(sampleVetResponse));

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].firstName").value("James"))
            .andExpect(jsonPath("$[0].lastName").value("Carter"))
            .andExpect(jsonPath("$[0].specialties[0].name").value("radiology"));
    }

    @Test
    void listVets_withLastNameFilter() throws Exception {
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(sampleVetResponse));

        mockMvc.perform(get("/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].lastName").value("Carter"));
    }

    @Test
    void listVets_withSpecialtyIdFilter() throws Exception {
        when(vetService.filterBySpecialty(1)).thenReturn(List.of(sampleVetResponse));

        mockMvc.perform(get("/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].specialties[0].id").value(1));
    }

    @Test
    void listVets_returnsEmptyList() throws Exception {
        when(vetService.getAllVets()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getVet_returnsVetById() throws Exception {
        when(vetService.getVetById(1)).thenReturn(sampleVetResponse);

        mockMvc.perform(get("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void getVet_returns404WhenNotFound() throws Exception {
        when(vetService.getVetById(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(get("/vets/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Resource Not Found"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void addVet_createsNewVet() throws Exception {
        when(vetService.createVet(any(VetRequestDto.class))).thenReturn(sampleVetResponse);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleVetRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.firstName").value("James"));
    }

    @Test
    void addVet_returns400WhenFirstNameBlank() throws Exception {
        VetRequestDto invalid = new VetRequestDto("", "Carter", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation Error"))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void addVet_returns400WhenLastNameBlank() throws Exception {
        VetRequestDto invalid = new VetRequestDto("James", "", List.of());

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    void addVet_returns400WhenSpecialtiesNull() throws Exception {
        VetRequestDto invalid = new VetRequestDto("James", "Carter", null);

        mockMvc.perform(post("/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("Validation Error"));
    }

    @Test
    void updateVet_updatesExistingVet() throws Exception {
        VetResponseDto updated = new VetResponseDto(1, "Helen", "Leary", List.of());
        VetRequestDto updateRequest = new VetRequestDto("Helen", "Leary", List.of());
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("Helen"));
    }

    @Test
    void updateVet_returns404WhenNotFound() throws Exception {
        VetRequestDto updateRequest = new VetRequestDto("Helen", "Leary", List.of());
        when(vetService.updateVet(eq(99), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(put("/vets/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_deletesAndReturnsVet() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(sampleVetResponse);

        mockMvc.perform(delete("/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteVet_returns404WhenNotFound() throws Exception {
        when(vetService.deleteVet(99)).thenThrow(new ResourceNotFoundException("Vet", 99));

        mockMvc.perform(delete("/vets/99"))
            .andExpect(status().isNotFound());
    }
}

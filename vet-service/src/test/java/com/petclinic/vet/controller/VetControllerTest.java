package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.VetDto;
import com.petclinic.vet.dto.VetRequestDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.VetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for VetController using @WebMvcTest.
 * Tests HTTP layer behaviour including request validation and error responses.
 */
@WebMvcTest(VetController.class)
@Import(GlobalExceptionHandler.class)
class VetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VetService vetService;

    @Autowired
    private ObjectMapper objectMapper;

    private VetDto sampleVetDto() {
        return new VetDto(1, "James", "Carter",
            List.of(new SpecialtyDto(1, "radiology")));
    }

    @Test
    void listVets_returnsOkWithList() throws Exception {
        when(vetService.listVets()).thenReturn(List.of(sampleVetDto()));

        mockMvc.perform(get("/api/vets"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].firstName", is("James")));
    }

    @Test
    void listVets_withSpecialtyId_filtersVets() throws Exception {
        when(vetService.findBySpecialty(1)).thenReturn(List.of(sampleVetDto()));

        mockMvc.perform(get("/api/vets").param("specialtyId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void listVets_withLastName_searchesVets() throws Exception {
        when(vetService.searchByLastName("Carter")).thenReturn(List.of(sampleVetDto()));

        mockMvc.perform(get("/api/vets").param("lastName", "Carter"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].lastName", is("Carter")));
    }

    @Test
    void getVet_existingId_returnsOk() throws Exception {
        when(vetService.getVet(1)).thenReturn(sampleVetDto());

        mockMvc.perform(get("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("James")))
            .andExpect(jsonPath("$.specialties", hasSize(1)));
    }

    @Test
    void getVet_nonExistingId_returns404() throws Exception {
        when(vetService.getVet(999))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(get("/api/vets/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")))
            .andExpect(jsonPath("$.detail", is("Vet not found with id: 999")));
    }

    @Test
    void addVet_validRequest_returnsCreated() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "Leary", List.of(1));
        VetDto created = new VetDto(2, "Helen", "Leary",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetService.createVet(any(VetRequestDto.class))).thenReturn(created);

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(2)))
            .andExpect(jsonPath("$.firstName", is("Helen")));
    }

    @Test
    void addVet_emptyFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("", "Leary", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Validation Failed")));
    }

    @Test
    void addVet_emptyLastName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto("Helen", "", List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addVet_nullFirstName_returns400() throws Exception {
        VetRequestDto request = new VetRequestDto();
        request.setLastName("Leary");
        request.setSpecialtyIds(List.of());

        mockMvc.perform(post("/api/vets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateVet_validRequest_returnsOk() throws Exception {
        VetRequestDto request = new VetRequestDto("Updated", "Name", List.of(1));
        VetDto updated = new VetDto(1, "Updated", "Name",
            List.of(new SpecialtyDto(1, "radiology")));
        when(vetService.updateVet(eq(1), any(VetRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/api/vets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName", is("Updated")));
    }

    @Test
    void updateVet_nonExistingId_returns404() throws Exception {
        VetRequestDto request = new VetRequestDto("Any", "Name", List.of());
        when(vetService.updateVet(eq(999), any(VetRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(put("/api/vets/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteVet_existingId_returnsOk() throws Exception {
        when(vetService.deleteVet(1)).thenReturn(sampleVetDto());

        mockMvc.perform(delete("/api/vets/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteVet_nonExistingId_returns404() throws Exception {
        when(vetService.deleteVet(999))
            .thenThrow(new ResourceNotFoundException("Vet not found with id: 999"));

        mockMvc.perform(delete("/api/vets/999"))
            .andExpect(status().isNotFound());
    }
}

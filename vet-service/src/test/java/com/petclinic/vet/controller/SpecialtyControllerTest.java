package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyDto;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
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
 * Integration tests for SpecialtyController using @WebMvcTest.
 * Tests HTTP layer behaviour including request validation and error responses.
 */
@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listSpecialties_returnsOkWithList() throws Exception {
        when(specialtyService.listSpecialties())
            .thenReturn(List.of(new SpecialtyDto(1, "radiology"), new SpecialtyDto(2, "surgery")));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_withNameParam_searchesByName() throws Exception {
        when(specialtyService.searchByName("rad"))
            .thenReturn(List.of(new SpecialtyDto(1, "radiology")));

        mockMvc.perform(get("/api/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.getSpecialty(1))
            .thenReturn(new SpecialtyDto(1, "radiology"));

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.getSpecialty(999))
            .thenThrow(new ResourceNotFoundException("Specialty not found with id: 999"));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")))
            .andExpect(jsonPath("$.detail", is("Specialty not found with id: 999")));
    }

    @Test
    void addSpecialty_validRequest_returnsCreated() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");
        when(specialtyService.createSpecialty(any(SpecialtyRequestDto.class)))
            .thenReturn(new SpecialtyDto(4, "cardiology"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(4)))
            .andExpect(jsonPath("$.name", is("cardiology")));
    }

    @Test
    void addSpecialty_emptyName_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Validation Failed")));
    }

    @Test
    void addSpecialty_nullName_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto();

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_validRequest_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-name");
        when(specialtyService.updateSpecialty(eq(1), any(SpecialtyRequestDto.class)))
            .thenReturn(new SpecialtyDto(1, "updated-name"));

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated-name")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.updateSpecialty(eq(999), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty not found with id: 999"));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.deleteSpecialty(1))
            .thenReturn(new SpecialtyDto(1, "radiology"));

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.deleteSpecialty(999))
            .thenThrow(new ResourceNotFoundException("Specialty not found with id: 999"));

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }
}

package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for {@link SpecialtyController} using @WebMvcTest.
 * The service layer is mocked to isolate controller/mapping/validation logic.
 */
@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SpecialtyService specialtyService;

    @Test
    @DisplayName("GET /api/specialties returns 200 with list of specialties")
    void listSpecialties_returnsOk() throws Exception {
        when(specialtyService.getAllSpecialties())
            .thenReturn(List.of(
                new SpecialtyResponseDto(1, "radiology"),
                new SpecialtyResponseDto(2, "surgery")));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    @DisplayName("GET /api/specialties returns 200 with empty list")
    void listSpecialties_returnsEmptyList() throws Exception {
        when(specialtyService.getAllSpecialties()).thenReturn(List.of());

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/specialties/{id} returns 200 when found")
    void getSpecialty_found() throws Exception {
        when(specialtyService.getSpecialtyById(1))
            .thenReturn(new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    @DisplayName("GET /api/specialties/{id} returns 404 when not found")
    void getSpecialty_notFound() throws Exception {
        when(specialtyService.getSpecialtyById(999))
            .thenThrow(new ResourceNotFoundException("Specialty not found with id: 999"));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Resource Not Found")));
    }

    @Test
    @DisplayName("POST /api/specialties returns 201 with valid body")
    void addSpecialty_createdSuccessfully() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        when(specialtyService.createSpecialty(any()))
            .thenReturn(new SpecialtyResponseDto(3, "dentistry"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("dentistry")));
    }

    @Test
    @DisplayName("POST /api/specialties returns 400 with blank name")
    void addSpecialty_blankName_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    @DisplayName("POST /api/specialties returns 400 with null name")
    void addSpecialty_nullName_returns400() throws Exception {
        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": null}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/specialties/{id} returns 200 when updated")
    void updateSpecialty_success() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        when(specialtyService.updateSpecialty(eq(1), any()))
            .thenReturn(new SpecialtyResponseDto(1, "updated-radiology"));

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated-radiology")));
    }

    @Test
    @DisplayName("PUT /api/specialties/{id} returns 404 when not found")
    void updateSpecialty_notFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated");
        when(specialtyService.updateSpecialty(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Specialty not found with id: 999"));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/specialties/{id} returns 200 when deleted")
    void deleteSpecialty_success() throws Exception {
        when(specialtyService.deleteSpecialty(1))
            .thenReturn(new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    @DisplayName("DELETE /api/specialties/{id} returns 404 when not found")
    void deleteSpecialty_notFound() throws Exception {
        when(specialtyService.deleteSpecialty(999))
            .thenThrow(new ResourceNotFoundException("Specialty not found with id: 999"));

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }
}

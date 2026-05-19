package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.DuplicateResourceException;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for SpecialtyController using @WebMvcTest.
 * Tests REST endpoint behavior, request validation, and error handling.
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
    void listSpecialties_returns200() throws Exception {
        SpecialtyResponseDto dto1 = new SpecialtyResponseDto(1, "radiology");
        SpecialtyResponseDto dto2 = new SpecialtyResponseDto(2, "surgery");
        when(specialtyService.getAllSpecialties()).thenReturn(List.of(dto1, dto2));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].id", is(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")))
            .andExpect(jsonPath("$[1].name", is("surgery")));
    }

    @Test
    @DisplayName("GET /api/specialties returns 200 with empty list")
    void listSpecialties_empty() throws Exception {
        when(specialtyService.getAllSpecialties()).thenReturn(List.of());

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("GET /api/specialties/{id} returns 200 when found")
    void getSpecialty_returns200() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.getSpecialtyById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    @DisplayName("GET /api/specialties/{id} returns 404 when not found")
    void getSpecialty_returns404() throws Exception {
        when(specialtyService.getSpecialtyById(99))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Resource Not Found")))
            .andExpect(jsonPath("$.detail").value("Specialty not found with id: 99"));
    }

    @Test
    @DisplayName("POST /api/specialties returns 201 on success")
    void addSpecialty_returns201() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("dentistry");
        SpecialtyResponseDto response = new SpecialtyResponseDto(3, "dentistry");
        when(specialtyService.createSpecialty(any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(3)))
            .andExpect(jsonPath("$.name", is("dentistry")));
    }

    @Test
    @DisplayName("POST /api/specialties returns 400 for blank name")
    void addSpecialty_blankName_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    @DisplayName("POST /api/specialties returns 400 for null name")
    void addSpecialty_nullName_returns400() throws Exception {
        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/specialties returns 409 for duplicate name")
    void addSpecialty_duplicate_returns409() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyService.createSpecialty(any(SpecialtyRequestDto.class)))
            .thenThrow(new DuplicateResourceException("Specialty with name 'radiology' already exists"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.title", is("Duplicate Resource")));
    }

    @Test
    @DisplayName("PUT /api/specialties/{id} returns 200 on success")
    void updateSpecialty_returns200() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "updated-radiology");
        when(specialtyService.updateSpecialty(eq(1), any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated-radiology")));
    }

    @Test
    @DisplayName("PUT /api/specialties/{id} returns 404 when not found")
    void updateSpecialty_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("test");
        when(specialtyService.updateSpecialty(eq(99), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/api/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/specialties/{id} returns 400 for invalid body")
    void updateSpecialty_invalidBody_returns400() throws Exception {
        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/specialties/{id} returns 204 on success")
    void deleteSpecialty_returns204() throws Exception {
        doNothing().when(specialtyService).deleteSpecialty(1);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/specialties/{id} returns 404 when not found")
    void deleteSpecialty_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty", 99))
            .when(specialtyService).deleteSpecialty(99);

        mockMvc.perform(delete("/api/specialties/99"))
            .andExpect(status().isNotFound());
    }
}

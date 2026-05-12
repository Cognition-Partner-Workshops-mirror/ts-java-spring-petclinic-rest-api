package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
import com.petclinic.vet.exception.DuplicateResourceException;
import com.petclinic.vet.exception.GlobalExceptionHandler;
import com.petclinic.vet.exception.ResourceNotFoundException;
import com.petclinic.vet.service.SpecialtyService;
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
 * Integration tests for SpecialtyController using @WebMvcTest.
 * Tests HTTP request/response mapping, validation, and error handling.
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
    void listSpecialties_returnsAllSpecialties() throws Exception {
        List<SpecialtyResponseDto> specialties = List.of(
            new SpecialtyResponseDto(1, "radiology"),
            new SpecialtyResponseDto(2, "surgery")
        );
        when(specialtyService.getAllSpecialties()).thenReturn(specialties);

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("radiology")))
            .andExpect(jsonPath("$[1].name", is("surgery")));
    }

    @Test
    void listSpecialties_withNameFilter_returnsFiltered() throws Exception {
        List<SpecialtyResponseDto> specialties = List.of(
            new SpecialtyResponseDto(1, "radiology")
        );
        when(specialtyService.searchByName("rad")).thenReturn(specialties);

        mockMvc.perform(get("/api/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialty_existingId_returnsSpecialty() throws Exception {
        SpecialtyResponseDto specialty = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.getSpecialtyById(1)).thenReturn(specialty);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.getSpecialtyById(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/api/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Resource Not Found")))
            .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void addSpecialty_validRequest_returnsCreatedSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("oncology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(4, "oncology");
        when(specialtyService.createSpecialty(any())).thenReturn(response);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(4)))
            .andExpect(jsonPath("$.name", is("oncology")));
    }

    @Test
    void addSpecialty_blankName_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Validation Error")))
            .andExpect(jsonPath("$.schemaValidationErrors").isArray());
    }

    @Test
    void addSpecialty_duplicateName_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyService.createSpecialty(any()))
            .thenThrow(new DuplicateResourceException("Specialty with name 'radiology' already exists"));

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title", is("Duplicate Resource")));
    }

    @Test
    void updateSpecialty_validRequest_returnsUpdatedSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "cardiology");
        when(specialtyService.updateSpecialty(eq(1), any())).thenReturn(response);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("cardiology")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("cardiology");
        when(specialtyService.updateSpecialty(eq(999), any()))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/api/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existingId_returnsDeletedSpecialty() throws Exception {
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.deleteSpecialty(1)).thenReturn(response);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.deleteSpecialty(999))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/api/specialties/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void addSpecialty_nullName_returns400() throws Exception {
        // JSON with null name to trigger validation
        String json = "{\"name\": null}";

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
            .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_nameTooLong_returns400() throws Exception {
        // Name exceeds the 80-character max
        String longName = "a".repeat(81);
        SpecialtyRequestDto request = new SpecialtyRequestDto(longName);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}

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
 * Integration tests for {@link SpecialtyController} using @WebMvcTest.
 * Tests HTTP-level request handling, validation, and error responses.
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
    void listSpecialties_shouldReturnAllSpecialties() throws Exception {
        when(specialtyService.getAllSpecialties()).thenReturn(List.of(
                new SpecialtyResponseDto(1, "radiology"),
                new SpecialtyResponseDto(2, "surgery")));

        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_shouldFilterByName() throws Exception {
        when(specialtyService.searchByName("radio")).thenReturn(List.of(
                new SpecialtyResponseDto(1, "radiology")));

        mockMvc.perform(get("/api/specialties").param("name", "radio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialty_shouldReturnSpecialty() throws Exception {
        when(specialtyService.getSpecialtyById(1)).thenReturn(
                new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(get("/api/specialties/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_shouldReturn404WhenNotFound() throws Exception {
        when(specialtyService.getSpecialtyById(99))
                .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/api/specialties/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Resource Not Found")));
    }

    @Test
    void addSpecialty_shouldCreateSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyService.createSpecialty(any())).thenReturn(
                new SpecialtyResponseDto(1, "radiology"));

        mockMvc.perform(post("/api/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void addSpecialty_shouldReturn400WhenNameBlank() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Request")));
    }

    @Test
    void addSpecialty_shouldReturn400WhenNameNull() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto();

        mockMvc.perform(post("/api/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSpecialty_shouldReturn409WhenDuplicate() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyService.createSpecialty(any()))
                .thenThrow(new DuplicateResourceException("Specialty", "name", "radiology"));

        mockMvc.perform(post("/api/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title", is("Duplicate Resource")));
    }

    @Test
    void updateSpecialty_shouldUpdateSpecialty() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        when(specialtyService.updateSpecialty(eq(1), any())).thenReturn(
                new SpecialtyResponseDto(1, "updated-radiology"));

        mockMvc.perform(put("/api/specialties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("updated-radiology")));
    }

    @Test
    void updateSpecialty_shouldReturn404WhenNotFound() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        when(specialtyService.updateSpecialty(eq(99), any()))
                .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/api/specialties/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateSpecialty_shouldReturn400WhenInvalid() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(put("/api/specialties/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteSpecialty_shouldReturn204() throws Exception {
        doNothing().when(specialtyService).deleteSpecialty(1);

        mockMvc.perform(delete("/api/specialties/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteSpecialty_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty", 99))
                .when(specialtyService).deleteSpecialty(99);

        mockMvc.perform(delete("/api/specialties/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addSpecialty_shouldReturn400WhenNameTooLong() throws Exception {
        String longName = "a".repeat(81);
        SpecialtyRequestDto request = new SpecialtyRequestDto(longName);

        mockMvc.perform(post("/api/specialties")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listSpecialties_shouldReturnEmptyList() throws Exception {
        when(specialtyService.getAllSpecialties()).thenReturn(List.of());

        mockMvc.perform(get("/api/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}

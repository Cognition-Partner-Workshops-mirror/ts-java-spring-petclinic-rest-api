package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.SpecialtyRequestDto;
import com.petclinic.vet.dto.SpecialtyResponseDto;
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
 * Tests REST endpoint mappings, request validation, and error handling.
 */
@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecialtyService specialtyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void listSpecialties_shouldReturnAll() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void listSpecialties_withNameFilter_shouldSearchByName() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.searchByName("rad")).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void listSpecialties_withBlankNameFilter_shouldReturnAll() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/specialties").param("name", "  "))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_shouldReturnSpecialty() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_notFound_shouldReturn404() throws Exception {
        when(specialtyService.findById(99)).thenThrow(new ResourceNotFoundException("Specialty not found with id: 99"));

        mockMvc.perform(get("/api/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("ResourceNotFoundException"))
            .andExpect(jsonPath("$.detail").value("Specialty not found with id: 99"));
    }

    @Test
    void addSpecialty_shouldCreateAndReturn201() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        SpecialtyResponseDto response = new SpecialtyResponseDto(2, "surgery");
        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(2))
            .andExpect(jsonPath("$.name").value("surgery"));
    }

    @Test
    void addSpecialty_withEmptyName_shouldReturn400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.title").value("MethodArgumentNotValidException"));
    }

    @Test
    void addSpecialty_withNullName_shouldReturn400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto();

        mockMvc.perform(post("/api/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_shouldUpdateAndReturn200() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("updated-radiology");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "updated-radiology");
        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated-radiology"));
    }

    @Test
    void updateSpecialty_notFound_shouldReturn404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("test");
        when(specialtyService.update(eq(99), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty not found with id: 99"));

        mockMvc.perform(put("/api/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_shouldReturn204() throws Exception {
        doNothing().when(specialtyService).delete(1);

        mockMvc.perform(delete("/api/specialties/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteSpecialty_notFound_shouldReturn404() throws Exception {
        doThrow(new ResourceNotFoundException("Specialty not found with id: 99"))
            .when(specialtyService).delete(99);

        mockMvc.perform(delete("/api/specialties/99"))
            .andExpect(status().isNotFound());
    }
}

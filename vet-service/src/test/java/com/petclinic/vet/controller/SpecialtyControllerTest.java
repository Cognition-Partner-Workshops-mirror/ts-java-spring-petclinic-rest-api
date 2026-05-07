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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpecialtyService specialtyService;

    private final SpecialtyResponseDto sampleSpecialty = new SpecialtyResponseDto(1, "radiology");

    @Test
    void listSpecialties_returnsOk() throws Exception {
        when(specialtyService.findAll()).thenReturn(List.of(sampleSpecialty));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void listSpecialties_filterByName() throws Exception {
        when(specialtyService.searchByName("rad")).thenReturn(List.of(sampleSpecialty));

        mockMvc.perform(get("/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void getSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.findById(1)).thenReturn(sampleSpecialty);

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.findById(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(get("/specialties/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("Not Found")));
    }

    @Test
    void addSpecialty_validRequest_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("radiology");
        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(sampleSpecialty);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void addSpecialty_invalidRequest_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_validRequest_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        SpecialtyResponseDto updated = new SpecialtyResponseDto(1, "surgery");
        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).thenReturn(updated);

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("surgery")));
    }

    @Test
    void updateSpecialty_nonExistingId_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto("surgery");
        when(specialtyService.update(eq(999), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(put("/specialties/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existingId_returnsOk() throws Exception {
        when(specialtyService.delete(1)).thenReturn(sampleSpecialty);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void deleteSpecialty_nonExistingId_returns404() throws Exception {
        when(specialtyService.delete(999)).thenThrow(new ResourceNotFoundException("Specialty", 999));

        mockMvc.perform(delete("/specialties/999"))
            .andExpect(status().isNotFound());
    }
}

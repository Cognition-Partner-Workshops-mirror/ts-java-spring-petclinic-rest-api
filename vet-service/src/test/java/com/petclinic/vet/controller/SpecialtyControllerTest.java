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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SpecialtyController.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Test
    void listSpecialties_shouldReturnAll() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void listSpecialties_withNameFilter_shouldReturnFiltered() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.searchByName("rad")).thenReturn(List.of(dto));

        mockMvc.perform(get("/specialties").param("name", "rad"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("radiology"));
    }

    @Test
    void getSpecialty_shouldReturnSpecialty() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findById(1)).thenReturn(dto);

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void getSpecialty_notFound_shouldReturn404() throws Exception {
        when(specialtyService.findById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title").value("Specialty not found"));
    }

    @Test
    void addSpecialty_shouldCreateSpecialty() throws Exception {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("radiology");
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("radiology"));
    }

    @Test
    void addSpecialty_invalidInput_shouldReturn400() throws Exception {
        SpecialtyRequestDto invalidDto = new SpecialtyRequestDto("");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_shouldUpdateSpecialty() throws Exception {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("surgery");
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(1, "surgery");
        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("surgery"));
    }

    @Test
    void updateSpecialty_notFound_shouldReturn404() throws Exception {
        SpecialtyRequestDto requestDto = new SpecialtyRequestDto("surgery");
        when(specialtyService.update(eq(99), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_shouldDeleteSpecialty() throws Exception {
        SpecialtyResponseDto responseDto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.delete(1)).thenReturn(responseDto);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteSpecialty_notFound_shouldReturn404() throws Exception {
        when(specialtyService.delete(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/specialties/99"))
            .andExpect(status().isNotFound());
    }
}

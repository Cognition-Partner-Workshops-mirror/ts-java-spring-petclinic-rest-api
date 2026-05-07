package com.petclinic.vet.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petclinic.vet.dto.request.SpecialtyRequestDto;
import com.petclinic.vet.dto.response.SpecialtyResponseDto;
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

@WebMvcTest(SpecialtyController.class)
@Import(GlobalExceptionHandler.class)
class SpecialtyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SpecialtyService specialtyService;

    @Test
    void listSpecialties_returnsAll() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/specialties"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name", is("radiology")));
    }

    @Test
    void getSpecialty_existing_returns200() throws Exception {
        SpecialtyResponseDto dto = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.findById(1)).thenReturn(dto);

        mockMvc.perform(get("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.name", is("radiology")));
    }

    @Test
    void getSpecialty_nonExisting_returns404() throws Exception {
        when(specialtyService.findById(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(get("/specialties/99"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.title", is("ResourceNotFoundException")));
    }

    @Test
    void addSpecialty_valid_returnsCreated() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "orthopedics");
        SpecialtyResponseDto response = new SpecialtyResponseDto(4, "orthopedics");
        when(specialtyService.create(any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(4)))
            .andExpect(jsonPath("$.name", is("orthopedics")))
            .andExpect(header().exists("Location"));
    }

    @Test
    void addSpecialty_invalidBody_returns400() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "");

        mockMvc.perform(post("/specialties")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateSpecialty_valid_returnsOk() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "updated");
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "updated");
        when(specialtyService.update(eq(1), any(SpecialtyRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/specialties/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name", is("updated")));
    }

    @Test
    void updateSpecialty_notFound_returns404() throws Exception {
        SpecialtyRequestDto request = new SpecialtyRequestDto(null, "xyz");
        when(specialtyService.update(eq(99), any(SpecialtyRequestDto.class)))
            .thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(put("/specialties/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteSpecialty_existing_returnsOk() throws Exception {
        SpecialtyResponseDto response = new SpecialtyResponseDto(1, "radiology");
        when(specialtyService.delete(1)).thenReturn(response);

        mockMvc.perform(delete("/specialties/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void deleteSpecialty_notFound_returns404() throws Exception {
        when(specialtyService.delete(99)).thenThrow(new ResourceNotFoundException("Specialty", 99));

        mockMvc.perform(delete("/specialties/99"))
            .andExpect(status().isNotFound());
    }
}
